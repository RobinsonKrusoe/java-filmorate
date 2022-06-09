package ru.yandex.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.filmorate.exceptions.EntityAlreadyExistException;
import ru.yandex.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.filmorate.model.Mpa;
import ru.yandex.filmorate.dao.EntityDao;

import java.util.ArrayList;
import java.util.List;

//Класс для хранения в базе справочника рейтингов фильмов
@Repository
public class MpaDaoImpl implements EntityDao<Mpa> {
    protected static final Logger log = LoggerFactory.getLogger(MpaDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    //Конструктор класса
    public MpaDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Mpa get(Integer id) {
        try {
            SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa where mpa_id = ?", id);
            if (mpaRows.next()) {
                Mpa mpa = new Mpa(mpaRows.getInt("mpa_id"),
                                  mpaRows.getString("name"));
                return mpa;
            }else{
                String errMess = "Рейтинг с идентификатором " + id + " не найден!";
                log.error(errMess);
                throw new EntityNotFoundException(errMess);
            }
        } catch (EmptyResultDataAccessException e) {
            String errMess = "Рейтинг с идентификатором " + id + " не найден!";
            log.error(errMess);
            throw new EntityNotFoundException(errMess);
        }
    }

    @Override
    public Mpa create(Mpa mpa) {
        try {
            jdbcTemplate.update("insert into mpa values (?,?)", mpa.getId(), mpa.getName());
            log.info("Создан рейтинг: " + mpa);
            return mpa;
        }catch (DuplicateKeyException e){
            String errMess = "Рейтинг с идентификатором " + mpa.getId() + " уже существует!";
            log.error(errMess);
            throw new EntityAlreadyExistException(errMess);
        }
    }

    @Override
    public void delete(Integer id) {
        Mpa mpa = get(id);
        jdbcTemplate.update("delete from mpa where mpa_id = ?", id);
        log.info("Удалён рейтинг " + mpa);
    }

    @Override
    public Mpa update(Mpa mpa) {
        get(mpa.getId());
        jdbcTemplate.update("update mpa set name = ? where mpa_id = ?", mpa.getName(), mpa.getId());
        log.info("Обновлён рейтинг: " + mpa);
        return mpa;
    }

    @Override
    public List<Mpa> getAll() {
        List<Mpa> ret = new ArrayList<>();
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa");

        while (mpaRows.next()) {
            ret.add(new Mpa(mpaRows.getInt("mpa_id"), mpaRows.getString("name")));
        }
        return ret;
    }
}
