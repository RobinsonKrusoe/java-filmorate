package ru.yandex.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.storage.user.UserStorage;

import java.util.List;

//Класс для операций с Пользователями
@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage){
        this.userStorage = userStorage;
    }

    //Возвращение всех пользователей
    public List<User> findAll(){
        return userStorage.findAll();
    }

    //Возвращение пользователя по запросу
    public User get(int id){
        return userStorage.get(id);
    }

    //Создание пользователя
    public User create(User user) {
        return userStorage.create(user);
    }

    //Обновление пользователя
    public User update(User user) {
        return userStorage.update(user);
    }

    //Добавление в друзья
    public void addFriend(Integer id, Integer friendId){
        userStorage.addFriend(id, friendId);
    }

    //Удаление из друзей
    public void delFriend(Integer id, Integer friendId){
        userStorage.delFriend(id, friendId);
    }

    //Возвращаем список пользователей, являющихся его друзьями
    public List<User> getFriends(Integer id){
        return userStorage.getFriends(id);
    }

    //Получение списка общих друзей
    public List<User> getCommonFriends(Integer id, Integer otherId){
        return userStorage.getCommonFriends(id, otherId);
    }
}
