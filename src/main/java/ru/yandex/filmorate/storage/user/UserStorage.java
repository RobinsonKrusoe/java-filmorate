package ru.yandex.filmorate.storage.user;

import ru.yandex.filmorate.model.User;

import java.util.List;
import java.util.Set;

//Интерфейс для работы с хранилищем Пользователей
public interface UserStorage {
    //Получение пользователя
    User get(Integer id);

    //Получение друзей пользователя
    Set<User> getFriends(Integer id);

    //Добавление пользователя
    User create(User user);

    //Удаление пользователя
    void delete(Integer id);

    //Обновление пользователя
    User update(User user);

    //Список пользователей
    List<User> findAll();
}
