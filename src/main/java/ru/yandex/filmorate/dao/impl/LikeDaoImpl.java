package ru.yandex.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.filmorate.dao.LinkDao;
import ru.yandex.filmorate.exceptions.EntityAlreadyExistException;
import ru.yandex.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.filmorate.model.Genre;
import ru.yandex.filmorate.model.Like;

import java.util.ArrayList;
import java.util.List;

//Класс для работы с лайками к фильму
@Repository
public class LikeDaoImpl implements LinkDao<Integer> {
    protected static final Logger log = LoggerFactory.getLogger(LikeDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    //Конструктор класса
    public LikeDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public void create(Integer userId, Integer filmId) {
        try {
            jdbcTemplate.update("insert into likes (user_id, film_id) values (?,?)", userId, filmId);
            //log.info("Пользователь " + userId + " поставил лайк фильму " + filmId);
        }catch (DuplicateKeyException e){
            String errMess = "Лайк пользователя " + userId + " на фильм " + filmId + " уже существует!";
            log.error(errMess);
            throw new EntityAlreadyExistException(errMess);
        }
    }

    @Override
    public void delete(Integer userId, Integer filmId) {
        Integer count = jdbcTemplate.update("delete from likes where user_id = ? and film_id = ?", userId, filmId);
        if(count == null || count == 0) {
            String errMess = "Лайк пользователя " + userId + " на фильм " + filmId + " отсутствует!";
            log.error(errMess);
            throw new EntityNotFoundException(errMess);
        }else{
            log.info("Удалён лайк пользователя " + userId + " на фильм " + filmId);
        }
    }

    //Приведение списка в базе к полученному
    @Override
    public void merge(Integer filmId, List<Integer> likes) {
        List<Integer> base = getFilmLikes(filmId);
        List<Integer> toDel = new ArrayList<>(base);
        List<Integer> toIns = new ArrayList<>();
        if(likes != null) toIns.addAll(likes);

        toDel.removeAll(toIns); //Список на удаление
        toIns.removeAll(base);  //Список на вставку

        for (Integer i : toDel)
            delete(i, filmId);

        for (Integer i : toIns)
            create(i, filmId);
    }

    //Получение списка мдентификаторов пользователей, кому понравился фильм
    public List<Integer> getFilmLikes(Integer filmId){
        List<Integer> ret = new ArrayList<>();
        SqlRowSet likeRows = jdbcTemplate.queryForRowSet("select * from likes where film_id = ?", filmId);

        while (likeRows.next()) {
            ret.add(likeRows.getInt("user_id"));
        }
        return ret;
    }
}
