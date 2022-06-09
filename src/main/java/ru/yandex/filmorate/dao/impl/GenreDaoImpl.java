package ru.yandex.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.filmorate.dao.EntityDao;
import ru.yandex.filmorate.exceptions.EntityAlreadyExistException;
import ru.yandex.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

//Класс для работы со справочником жанров фильма
@Repository
public class GenreDaoImpl implements EntityDao<Genre> {
    protected static final Logger log = LoggerFactory.getLogger(GenreDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    //Конструктор класса
    public GenreDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Genre get(Integer id) {
        try {
            SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", id);
            if (genreRows.next()) {
                Genre genre = new Genre(genreRows.getInt("genre_id"),
                                        genreRows.getString("name"));
                return genre;
            }else{
                String errMess = "Жанр с идентификатором " + id + " не найден!";
                log.error(errMess);
                throw new EntityNotFoundException(errMess);
            }
        } catch (EmptyResultDataAccessException e) {
            String errMess = "Жанр с идентификатором " + id + " не найден!";
            log.error(errMess);
            throw new EntityNotFoundException(errMess);
        }
    }

    @Override
    public Genre create(Genre genre) {
        try {
            jdbcTemplate.update("insert into genre values (?,?)", genre.getId(), genre.getName());
            log.info("Создан жанр: " + genre);
            return genre;
        }catch (DuplicateKeyException e){
            String errMess = "Жанр с идентификатором " + genre.getId() + " уже существует!";
            log.error(errMess);
            throw new EntityAlreadyExistException(errMess);
        }
    }

    @Override
    public void delete(Integer id) {
        Genre genre = get(id);
        jdbcTemplate.update("delete from genre where genre_id = ?", id);
        log.info("Удалён рейтинг " + genre);
    }

    @Override
    public Genre update(Genre genre) {
        get(genre.getId());
        jdbcTemplate.update("update genre set name = ? where genre_id = ?", genre.getName(), genre.getId());
        log.info("Обновлён рейтинг: " + genre);
        return genre;
    }

    @Override
    public List<Genre> getAll() {
        List<Genre> ret = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genre");

        while (genreRows.next()) {
            ret.add(new Genre(genreRows.getInt("genre_id"), genreRows.getString("name")));
        }
        return ret;
    }
}
