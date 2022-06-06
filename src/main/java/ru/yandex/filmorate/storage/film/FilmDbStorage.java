package ru.yandex.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

//Класс для реализации интерфейса для работы с хранилищем Фильмов (реализация для базы данных)
@Component("FilmDbStorage")
@Repository
public class FilmDbStorage implements FilmStorage{
    protected static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    //Конструктор класса
    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    //Получение фильма
    @Override
    public Film get(Integer id) {
        try {
            SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where film_id = ?", id);
            if (filmRows.next()) {
                Film film = new Film(filmRows.getInt("film_id"),
                        filmRows.getString("name"),
                        filmRows.getString("description"),
                        filmRows.getDate("release_date").toLocalDate(),
                        filmRows.getInt("duration"),
                        Film.MPA.valueOf(filmRows.getString("mpa_raiting")));
                return film;
            }else{
                log.error("Фильм с идентификатором " + id + " не найден!");
                throw new FilmNotFoundException("Фильм с идентификатором " + id + " не найден!");
            }
        } catch (EmptyResultDataAccessException e) {
            log.error("Фильм с идентификатором " + id + " не найден!");
            throw new FilmNotFoundException("Фильм с идентификатором " + id + " не найден!");
        }
    }

    //Добавление фильма
    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        checkIfNameBusy(film);
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement("insert into film (name, description, release_date, duration, mpa_raiting)" +
                                        " values (?,?,?,?,?)", new String[]{"film_id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
                ps.setInt(4, film.getDuration());
                ps.setString(5, film.getMpa().name());
                return ps;
            }, keyHolder);

            film.setId(keyHolder.getKey().intValue());
            log.info("Создан фильм: " + film);
            return film;
        }catch (DuplicateKeyException e){
            log.error("Фильм с идентификатором " + film.getId() + " уже существует!");
            throw new FilmAlreadyExistException("Фильм с идентификатором " + film.getId() + " уже существует!");
        }
    }

    //Удаление фильма
    @Override
    public void delete(Integer id) {
        get(id);
        jdbcTemplate.update("delete from film where film_id = ?", id);
        log.info("Удалён фильм  #" + id);
    }

    //Обновление фильма
    @Override
    public Film update(Film film) {
        get(film.getId());

        checkIfNameBusy(film);
        try {
            jdbcTemplate.update("update film set name = ?, " +
                                                    "description = ?, " +
                                                    "release_date = ?, " +
                                                    "duration = ?, " +
                                                    "mpa_raiting = ?" +
                                    " where film_id = ?",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().name(),
                    film.getId());

            log.info("Обновлён фильм: " + film);
            return film;
        } catch (EmptyResultDataAccessException e) {
            log.error("Фильм с идентификатором " + film.getId() + " не найден!");
            throw new FilmNotFoundException("Фильм с идентификатором " + film.getId() + " не найден!");
        }
    }

    //Возвращение всех фильмов
    @Override
    public List<Film> findAll() {
        List<Film> ret = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film");

        while (filmRows.next()) {
            ret.add(new Film(filmRows.getInt("film_id"),
                             filmRows.getString("name"),
                             filmRows.getString("description"),
                             filmRows.getDate("release_date").toLocalDate(),
                             filmRows.getInt("duration"),
                             Film.MPA.valueOf(filmRows.getString("mpa_raiting"))));
        }
        return ret;
    }

    //Процедура проверки сущности перед обновлением на конфликт новой версии с другими сущностями в базе
    public void checkIfNameBusy(Film film){
        SqlRowSet filmRows = jdbcTemplate
                .queryForRowSet("select count(*) c " +
                                      "from film " +
                                     "where film_id != ? and name = ? and release_date = ?",
                film.getId(), film.getName(), film.getReleaseDate());
        if(filmRows.next() && filmRows.getInt("c") > 0){
            log.error("Фильм с названием \"" + film.getName() +
                    "\" с датой выпуска " + film.getReleaseDate() + " уже существует!");
            throw new FilmAlreadyExistException("Фильм с названием \"" + film.getName() +
                    "\" с датой выпуска " + film.getReleaseDate() + " уже существует!");
        }
    }

    //Добавление лайка фильму
    public void addLike(Film film, User user){
        jdbcTemplate.update("MERGE INTO likes l KEY (film_id, user_id) VALUES (?, ?)", film.getId(), user.getId());
        log.info("Пользователь \"" + user.getName() + "\" добавил лайк к фильму \"" + film.getName() + "\".");
    }

    //Удаление лайка у фильма
    public void delLike(Film film, User user){
        try {
            jdbcTemplate.update("DELETE FROM likes l WHERE film_id = ? AND user_id = ?", film.getId(), user.getId());
            log.info("Пользователь \"" + user.getName() + "\" удалил свой лайк к фильму \"" + film.getName() + "\".");
        } catch (EmptyResultDataAccessException e) {
            log.warn("Попытка удалить несуществующий лайк к фильму \"" +
                    film.getName() + "\" от пользователя " + user.getName() + "!");
        }
    }

    //Получение списка наиболее популярных фильмов
    public List<Film> getMostPopular(Integer count){
        List<Film> ret = new ArrayList<>();
        SqlRowSet filmRows;
        String SQL = "select * from film f ORDER BY (SELECT count(*) FROM likes l WHERE l.film_id = f.film_id) desc";

        if (count == null || count == 0)
            filmRows = jdbcTemplate.queryForRowSet(SQL);
        else
            filmRows = jdbcTemplate.queryForRowSet(SQL + " limit ?", count);

        while (filmRows.next()) {
            ret.add(new Film(filmRows.getInt("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    Film.MPA.valueOf(filmRows.getString("mpa_raiting"))));
        }
        return ret;
    }
}
