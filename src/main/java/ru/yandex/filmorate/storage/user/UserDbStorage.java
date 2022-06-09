package ru.yandex.filmorate.storage.user;

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
import ru.yandex.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.filmorate.exceptions.UserNotFoundException;
import ru.yandex.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

//Класс для реализации интерфейса для работы с хранилищем Пользователей (реализация для базы данных)
@Component("UserDbStorage")
@Repository
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    //Конструктор хранилища
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Получение пользователя
    @Override
    public User get(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from \"user\" where user_id = ?", id);
        if (userRows.next()) {
            User user = new User(userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());

            return user;
        } else {
            log.error("Пользователь #" + id + " не найден!");
            throw new UserNotFoundException("Пользователь #" + id + " не найден!");
        }
    }

    //Добавление пользователя
    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement("insert into \"user\" (email, login, name, birthday) values (?,?,?,?)",
                                new String[]{"user_id"});
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
                return ps;
            }, keyHolder);

            user.setId(keyHolder.getKey().intValue());
            log.info("Создан пользователь: " + user);
            return user;
        } catch (DuplicateKeyException e) {
            mapDupUserException(user, e);
            throw e;
        }
    }

    //Удаление пользователя
    @Override
    public void delete(Integer id) {
        get(id);
        jdbcTemplate.update("delete from \"user\" where user_id = ?", id);
        log.info("Удалён пользователь #" + id);
    }

    //обновление пользователя
    @Override
    public User update(User user) {
        get(user.getId());
        try {
            jdbcTemplate.update("update \"user\" set email = ?, login = ?, name = ?, birthday = ? where user_id = ?",
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());

            log.info("Изменён пользователь: " + user);
            return user;
        } catch (DuplicateKeyException e) {
            mapDupUserException(user, e);
            throw e;
        }
    }

    //Возвращение всех пользователей
    @Override
    public List<User> findAll() {
        List<User> ret = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from \"user\"");

        while (userRows.next()) {
            ret.add(new User(userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()));
        }

        return ret;
    }

    //Получение друзей пользователя
    @Override
    public List<User> getFriends(Integer id) {
        List<User> ret = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate
                .queryForRowSet("select * " +
                        "from \"user\" " +
                        "where user_id in (select friend_id from friends where user_id = ?)", id);

        while (userRows.next()) {
            ret.add(new User(userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()));
        }

        return ret;
    }

    //Добавление в друзья
    @Override
    public void addFriend(Integer id, Integer friendId) {
        User user = get(id);
        User friend = get(friendId);

        jdbcTemplate.update("MERGE INTO friends f KEY (user_id, friend_id) VALUES (?, ?)", id, friendId);
        log.info("Пользователь \"" + user.getName() + "\" добавил в друзья \"" + friend.getName() + "\".");
    }

    //Удаление из друзей
    @Override
    public void delFriend(Integer id, Integer friendId) {
        User user = get(id);
        User friend = get(friendId);
        try {
            jdbcTemplate.update("DELETE FROM friends f WHERE friend_id = ? AND user_id = ?", friendId, id);
            log.info("Пользователь: \"" + user.getName() + "\" удалил из друзей пользователя \"" + friend.getName() + "\".");
        } catch (EmptyResultDataAccessException e) {
            log.warn("Попытка удалить несуществующую дружбу \"" + user.getName() + "\" -> \"" + friend.getName() + "\"!");
        }
    }

    //Получение списка общих друзей
    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        User user = get(id);
        User friend = get(otherId);
        List<User> ret = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT *" +
                " FROM \"user\" u" +
                " WHERE u.user_id in (SELECT friend_id" +
                " FROM friends f" +
                " WHERE f.user_id = ?)" +
                " AND u.user_id in (SELECT friend_id" +
                " FROM friends f" +
                " WHERE f.user_id = ?)", id, otherId);

        while (userRows.next()) {
            ret.add(new User(userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()));
        }

        return ret;
    }

    //Обработка исключений дублирования при вставке и обновлении пользователя
    private void mapDupUserException(User user, DuplicateKeyException e) {
        if (e.toString().contains("IDX_USER_EMAIL")) {
            log.error("Пользователь с email \"" + user.getEmail() + "\" уже существует!");
            throw new UserAlreadyExistException("Пользователь с email \"" + user.getEmail() + "\" уже существует!");
        }

        if (e.toString().contains("IDX_USER_LOGIN")) {
            log.error("Пользователь с логином \"" + user.getLogin() + "\" уже существует!");
            throw new UserAlreadyExistException("Пользователь с email \"" + user.getEmail() + "\" уже существует!");
        }

        if (e.toString().contains("PK_FILM")) {
            log.error("Пользователь с номером #" + user.getId() + " уже существует!" + e.toString());
            throw new UserAlreadyExistException("Пользователь с номером #" + user.getId() + " уже существует!" + e.toString());
        }
    }
}
