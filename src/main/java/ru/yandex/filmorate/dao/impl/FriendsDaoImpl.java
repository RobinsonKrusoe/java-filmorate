package ru.yandex.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.filmorate.dao.LinkDao;
import ru.yandex.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.filmorate.model.Genre;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

//Класс для работы с дружескими связями между пользователями
@Repository
public class FriendsDaoImpl implements LinkDao<Integer> {
    protected static final Logger log = LoggerFactory.getLogger(FriendsDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    //Конструктор класса
    public FriendsDaoImpl(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage){
        this.jdbcTemplate=jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    //Добавление в друзья
    @Override
    public void create(Integer userId, Integer friendId) {
        User user = userDbStorage.get(userId);
        User friend = userDbStorage.get(friendId);

        jdbcTemplate.update("MERGE INTO friends f KEY (user_id, friend_id) VALUES (?, ?)", userId, friendId);
        log.info("Пользователь \"" + user.getName() + "\" добавил в друзья \"" + friend.getName() + "\".");
    }

    //Удаление из друзей
    @Override
    public void delete(Integer userId, Integer friendId) {
        User user = userDbStorage.get(userId);
        User friend = userDbStorage.get(friendId);

        Integer count = jdbcTemplate.update("DELETE FROM friends f WHERE friend_id = ? AND user_id = ?",
                                            friendId, userId);

        if(count == null || count == 0) {
            String errMess = "Попытка удалить несуществующую дружбу \"" +
                             user.getName() + "\" -> \"" + friend.getName() + "\"!";
            log.error(errMess);
            throw new EntityNotFoundException(errMess);
        }else{
            log.info("Пользователь: \"" + user.getName() +
                     "\" удалил из друзей пользователя \"" + friend.getName() + "\".");
        }
    }

    @Override
    public void merge(Integer userId, List<Integer> friends) {
        List<Integer> base = new ArrayList<>();
        List<Integer> toDel = new ArrayList<>();
        List<Integer> toIns = new ArrayList<>();

        for(User u : getUserFriends(userId))
            base.add(u.getId());

        if(friends != null) toIns.addAll(friends);

        toDel.removeAll(toIns); //Список на удаление
        toIns.removeAll(base);  //Список на вставку

        for (Integer i : toDel)
            delete(userId, i);

        for (Integer i : toIns)
            create(userId, i);
    }

    //Получение друзей пользователя
    public List<User> getUserFriends(Integer userId) {
        List<User> ret = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate
                .queryForRowSet("select * " +
                        "from \"user\" " +
                        "where user_id in (select friend_id from friends where user_id = ?)", userId);

        while (userRows.next()) {
            ret.add(new User(userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()));
        }

        return ret;
    }

    //Получение списка общих друзей
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = userDbStorage.get(userId);
        User friend = userDbStorage.get(otherId);
        List<User> ret = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT *" +
                " FROM \"user\" u" +
                " WHERE u.user_id in (SELECT friend_id" +
                " FROM friends f" +
                " WHERE f.user_id = ?)" +
                " AND u.user_id in (SELECT friend_id" +
                " FROM friends f" +
                " WHERE f.user_id = ?)", userId, otherId);

        while (userRows.next()) {
            ret.add(new User(userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()));
        }

        return ret;
    }
}
