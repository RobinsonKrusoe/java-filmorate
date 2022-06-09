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
import ru.yandex.filmorate.dao.impl.FilmGenreDaoImpl;
import ru.yandex.filmorate.dao.impl.LikeDaoImpl;
import ru.yandex.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.model.Genre;
import ru.yandex.filmorate.model.Mpa;
import ru.yandex.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

//Класс для реализации интерфейса для работы с хранилищем Фильмов (реализация для базы данных)
@Component("FilmDbStorage")
@Repository
public class FilmDbStorage implements FilmStorage{
    protected static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final LikeDaoImpl likeDaoImpl;
    private final FilmGenreDaoImpl filmGenreDaoImpl;
    private final JdbcTemplate jdbcTemplate;

    //Конструктор класса
    public FilmDbStorage(LikeDaoImpl likeDaoImpl, FilmGenreDaoImpl filmGenreDaoImpl, JdbcTemplate jdbcTemplate){
        this.likeDaoImpl = likeDaoImpl;
        this.filmGenreDaoImpl = filmGenreDaoImpl;
        this.jdbcTemplate=jdbcTemplate;
    }

    //Получение фильма
    @Override
    public Film get(Integer id) {
        try {
            SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from v_film_mpa where film_id = ?", id);
            if (filmRows.next()) {
                Film film = new Film();
                film.setId(filmRows.getInt("film_id"));
                film.setName(filmRows.getString("name"));
                film.setDescription(filmRows.getString("description"));
                film.setReleaseDate(filmRows.getDate("release_date").toLocalDate());
                film.setDuration(filmRows.getInt("duration"));
                film.setMpa(new Mpa(filmRows.getInt("mpa_id"),
                                    filmRows.getString("mpa_name")));
                List<Genre> genres = filmGenreDaoImpl.getFilmGenres(id);
                film.setGenres((genres.size() == 0)?null:genres);
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
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement("insert into film (name, description, release_date, duration, mpa_id)" +
                                        " values (?,?,?,?,?)", new String[]{"film_id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
                ps.setInt(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, keyHolder);

            film.setId(keyHolder.getKey().intValue());
            log.info("Создан фильм: " + film);
            if(film.getGenres() != null)
                filmGenreDaoImpl.merge(film.getId(), film.getGenres());
            return film;
        }catch (DuplicateKeyException e){
            mapDupFilmException(film, e);
            throw e;
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
        try {
            jdbcTemplate.update("update film set name = ?, " +
                                                    "description = ?, " +
                                                    "release_date = ?, " +
                                                    "duration = ?, " +
                                                    "mpa_id = ?" +
                                    " where film_id = ?",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

            filmGenreDaoImpl.merge(film.getId(), film.getGenres());

            if(film.getGenres() != null) {
                film.setGenres(filmGenreDaoImpl.getFilmGenres(film.getId()));
            }

            log.info("Обновлён фильм: " + film);
            return film;
        }catch (DuplicateKeyException e){
            mapDupFilmException(film, e);
            throw e;
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
                             new Mpa(filmRows.getInt("mpa_id"))));
        }
        return ret;
    }

    //Добавление лайка фильму
    public void addLike(Film film, User user){
        likeDaoImpl.create(user.getId(), film.getId());
        log.info("Пользователь \"" + user.getName() + "\" добавил лайк к фильму \"" + film.getName() + "\".");
    }

    //Удаление лайка у фильма
    public void delLike(Film film, User user){
        likeDaoImpl.delete(user.getId(), film.getId());
        log.info("Пользователь \"" + user.getName() + "\" удалил свой лайк к фильму \"" + film.getName() + "\".");
    }

    //Получение списка наиболее популярных фильмов
    public List<Film> getMostPopular(Integer count){
        List<Film> ret = new ArrayList<>();
        SqlRowSet filmRows;
        String SQL = "select * from film f order by (select count(*) from likes l where l.film_id = f.film_id) desc";

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
                    new Mpa(filmRows.getInt("mpa_id"))));
        }
        return ret;
    }

    //Обработка исключений дублирования при вставке и обновлении фильма
    private void mapDupFilmException(Film film, DuplicateKeyException e) {
        if (e.toString().contains("PK_FILM")) {
            log.error("Фильм с идентификатором " + film.getId() + " уже существует!");
            throw new FilmAlreadyExistException("Фильм с идентификатором " + film.getId() + " уже существует!");
        }

        if (e.toString().contains("IDX_FILM_NAME_DATE")) {
            log.error("Фильм с названием \"" + film.getName() +
                    "\" с датой выпуска " + film.getReleaseDate() + " уже существует!");
            throw new FilmAlreadyExistException("Фильм с названием \"" + film.getName() +
                    "\" с датой выпуска " + film.getReleaseDate() + " уже существует!");
        }
    }

}
