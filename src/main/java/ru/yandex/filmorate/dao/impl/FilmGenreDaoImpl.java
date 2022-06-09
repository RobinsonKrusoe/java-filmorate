package ru.yandex.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.filmorate.dao.LinkDao;
import ru.yandex.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.filmorate.model.*;

import java.util.ArrayList;
import java.util.List;


//Класс для работы с принодлежностью фильма к определённым жанрам
@Repository
public class FilmGenreDaoImpl implements LinkDao <Genre>{
    protected static final Logger log = LoggerFactory.getLogger(FilmGenreDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;
    //private final FilmDbStorage filmDbStorage;
    private final GenreDaoImpl genreDaoImpl;

    //Конструктор класса
    public FilmGenreDaoImpl(JdbcTemplate jdbcTemplate, //FilmDbStorage filmDbStorage,
                            GenreDaoImpl genreDaoImpl){
        this.jdbcTemplate=jdbcTemplate;
        //this.filmDbStorage = filmDbStorage;
        this.genreDaoImpl = genreDaoImpl;
    }

    //Отнесение фильма к жанру
    @Override
    public void create(Integer filmId, Integer genreId) {
        Genre genre = genreDaoImpl.get(genreId);

        jdbcTemplate.update("MERGE INTO film_genre KEY (film_id, genre_id) VALUES (?, ?)", filmId, genreId);
        log.info("фильму " + filmId + " добавлен жанр \"" + genre.getName() + "\".");
    }

    //Исключение фильма из жанра
    @Override
    public void delete(Integer filmId, Integer genreId) {
        Genre genre = genreDaoImpl.get(genreId);

        Integer count = jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?",
                filmId, genreId);

        if(count == null || count == 0) {
            String errMess = "Попытка удалить несуществующий жанр \"" +
                    genre.getName() + "\" у фильма " + filmId + "!";
            log.error(errMess);
            throw new EntityNotFoundException(errMess);
        }else{
            log.info("Фильм: " + filmId +
                    " исключён из жанра \"" + genre.getName() + "\".");
        }
    }

    //Приведение списка в базе к полученному
    @Override
    public void merge(Integer filmId, List<Genre> genres) {
        List<Integer> base = new ArrayList<>();
        List<Integer> toDel = new ArrayList<>();
        List<Integer> toIns = new ArrayList<>();

        for(Genre g : getFilmGenres(filmId))
            base.add(g.getId());

        if (genres != null)
            for(Genre g : genres)
                toIns.add(g.getId());

        toDel.addAll(base);
        toDel.removeAll(toIns); //Список на удаление
        toIns.removeAll(base);  //Список на вставку

        for (Integer i : toDel)
            delete(filmId, i);

        for (Integer i : toIns)
            create(filmId, i);
    }

    //Получение списка жанров фильма
    public List<Genre> getFilmGenres(Integer filmId){
        List<Genre> ret = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select g.* from genre g " +
                                                          "join film_genre fg on fg.genre_id = g.genre_id " +
                                                          "where fg.film_id = ?", filmId);

        while (genreRows.next()) {
            ret.add(new Genre(genreRows.getInt("genre_id"),
                              genreRows.getString("name")));
        }
        return ret;
    }
}
