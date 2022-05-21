package ru.yandex.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//Класс для операций с Пользователями
@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage){
        this.userStorage = userStorage;
    }
    //Добавление в друзья
    public void addFriend(User user, User friend){
        if(user.getFriends() == null) user.setFriends(new HashSet<>());
        if(friend.getFriends() == null) friend.setFriends(new HashSet<>());

        if (!user.getFriends().contains(friend.getId())) {
            user.getFriends().add(friend.getId());
            if(!friend.getFriends().contains(user.getId()))
                friend.getFriends().add(user.getId());
        }
    }

    //Удаление из друзей
    public void delFriend(User user, User friend){
        if (user.getFriends() != null && user.getFriends().contains(friend.getId())) {
            user.getFriends().remove(friend.getId());
            if(friend.getFriends() != null && friend.getFriends().contains(user.getId()))
                friend.getFriends().remove(user.getId());
        }
    }

    //Получение списка общих друзей
    public List<User> getCommonFriends(Integer id, Integer otherId){
        User user = userStorage.get(id);
        User other = userStorage.get(otherId);

        List<User> ret = new ArrayList<>();
        if (user.getFriends() != null && other.getFriends() != null){
            for(Integer i : user.getFriends()){
                if(other.getFriends().contains(i))
                    ret.add(userStorage.get(i));
            }
        }
        return ret;
    }
}
