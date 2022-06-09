package ru.yandex.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.yandex.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.filmorate.exceptions.UserNotFoundException;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.storage.InMemoryItemStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//Класс для реализации интерфейса для работы с хранилищем Пользователей
@Component
public class InMemoryUserStorage extends InMemoryItemStorage<User> implements UserStorage{
    protected static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    //Получение пользователя
    @Override
    public User get(Integer id){
        if(items.containsKey(id))
            return items.get(id);
        else{
            log.error("Пользователь #" + id + " не найден!");
            throw new UserNotFoundException("Пользователь #" + id + " не найден!");
        }
    }

    //Получение друзей пользователя
    public List<User> getFriends(Integer id){
        List<User> ret = new ArrayList<>();
        if(get(id).getFriends() != null){
            for(Integer i : get(id).getFriends())
                ret.add(get(i));
        }

        return ret;
    }

    //Добавление пользователя
    @Override
    public User create(User user){
        //Проверка занятости идентификатора
        if (items.containsKey(user.getId())) {
            log.error("Пользователь с номером #" + user.getId() + " уже существует!");
            throw new UserAlreadyExistException("Пользователь с номером #" + user.getId() + " уже существует!");
        }

        for (User userInFor: items.values()){
            if(userInFor.getEmail().equals(user.getEmail())){
                log.error("Пользователь с Email " + userInFor.getEmail() + " уже существует!");
                throw new UserAlreadyExistException("Пользователь с Email " + userInFor.getEmail() + " уже существует!");
            }

            if(userInFor.getLogin().equals(user.getLogin())){
                log.error("Логин " + userInFor.getLogin() + " уже занят!");
                throw new UserAlreadyExistException("Логин " + userInFor.getLogin() + " уже занят!");
            }
        }

        //Если идентификатор пользователя не задан - сгенерировать его автоматически
        if (user.getId() <= 0)
            user.setId(calcNewNum());

        items.put(user.getId(), user);    //Вставить фильм в список
        log.info("Создан пользователь: " + user);

        return user;
    }

    //обновление пользователя
    @Override
    public User update(User user) {
        if (items.containsKey(user.getId())) {
            for (User userInFor: items.values()) {
                if (user.getId() != userInFor.getId()) {
                    if (userInFor.getEmail().equals(user.getEmail())) {
                        log.error("Пользователь с Email" + userInFor.getEmail() + " уже существует!");
                        throw new UserAlreadyExistException("Пользователь с Email " + userInFor.getEmail() + " уже существует!");
                    }

                    if (userInFor.getLogin().equals(user.getLogin())) {
                        log.error("Логин " + userInFor.getLogin() + " уже занят!");
                        throw new UserAlreadyExistException("Логин " + userInFor.getLogin() + " уже занят!");
                    }
                }
            }

            items.replace(user.getId(), user);
            log.info("Изменён пользователь: " + user);
            return user;
        } else {
            log.error("Пользователь с идентификатором " + user.getId() + " не существует!");
            throw new UserNotFoundException("Пользователь с идентификатором " + user.getId() + " не существует!");
        }
    }

    //Удаление пользователя
    @Override
    public void delete(Integer id){
        if(items.containsKey(id))
            items.remove(id);
        else{
            log.error("Пользователь #" + id + " не найден!");
            throw new UserNotFoundException("Пользователь #" + id + " не найден!");
        }
    }

    //Добавление в друзья
    public void addFriend(Integer id, Integer friendId){
        User user = get(id);
        User friend = get(friendId);

        if(user.getFriends() == null) user.setFriends(new HashSet<>());
        if(friend.getFriends() == null) friend.setFriends(new HashSet<>());

        if (!user.getFriends().contains(friend.getId())) {
            user.getFriends().add(friend.getId());
            if(!friend.getFriends().contains(user.getId()))
                friend.getFriends().add(user.getId());
        }
    }

    //Удаление из друзей
    public void delFriend(Integer id, Integer friendId){
        User user = get(id);
        User friend = get(friendId);

        if (user.getFriends() != null && user.getFriends().contains(friend.getId())) {
            user.getFriends().remove(friend.getId());
            if(friend.getFriends() != null && friend.getFriends().contains(user.getId()))
                friend.getFriends().remove(user.getId());
        }
    }

    //Получение списка общих друзей
    public List<User> getCommonFriends(Integer id, Integer otherId){
        User user = get(id);
        User other = get(otherId);

        List<User> ret = new ArrayList<>();
        if (user.getFriends() != null && other.getFriends() != null){
            for(Integer i : user.getFriends()){
                if(other.getFriends().contains(i))
                    ret.add(get(i));
            }
        }
        return ret;
    }
}
