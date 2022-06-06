package ru.yandex.filmorate.storage.user;

import ru.yandex.filmorate.model.User;

import java.util.List;

//Интерфейс для работы с хранилищем Пользователей
public interface UserStorage {
    //Получение пользователя
    User get(Integer id);

    //Добавление пользователя
    User create(User user);

    //Удаление пользователя
    void delete(Integer id);

    //Обновление пользователя
    User update(User user);

    //Список пользователей
    List<User> findAll();

    //Получение друзей пользователя
    List<User> getFriends(Integer id);

    //Добавление в друзья
    void addFriend(Integer id, Integer friendId);

    //Удаление из друзей
    void delFriend(Integer id, Integer friendId);

    //Получение списка общих друзей
    List<User> getCommonFriends(Integer id, Integer otherId);
}
