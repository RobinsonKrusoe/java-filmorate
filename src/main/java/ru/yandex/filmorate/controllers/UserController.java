package ru.yandex.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.service.user.UserService;
import ru.yandex.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

//Класс контроллера для работы с пользователями
@RestController
@RequestMapping("/users")
public class UserController{
    private UserService userService;
    private UserStorage userStorage;
    @Autowired
    public UserController(UserService userService, UserStorage userStorage){
        this.userService = userService;
        this.userStorage = userStorage;
    }

    //Возвращение всех пользователей
    @GetMapping
    public List<User> findAll(){
        return userStorage.findAll();
    }

    //Возвращение пользователя по запросу
    @GetMapping("/{id}")
    public User findUser(@PathVariable int id){
        return userStorage.get(id);
    }

    //Создание пользователя;
    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return userStorage.create(user);
    }

    //Обновление пользователя;
    @PutMapping
    public User update(@RequestBody @Valid User user) {
        return userStorage.update(user);
    }

    //Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(userStorage.get(id), userStorage.get(friendId));
    }

    //Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void delFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.delFriend(userStorage.get(id), userStorage.get(friendId));
    }

    //Возвращаем список пользователей, являющихся его друзьями
    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable int id){
        return userStorage.getFriends(id);
    }

    //Возвращаем список друзей, общих с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId){
        return userService.getCommonFriends(id, otherId);
    }
}
