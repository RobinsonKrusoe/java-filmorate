package ru.yandex.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;

//Класс контроллера для работы с пользователями
@RestController
@RequestMapping("/users")
public class UserController{
    private UserService userService;
    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    //Возвращение всех пользователей
    @GetMapping
    public List<User> findAll(){
        return userService.findAll();
    }

    //Возвращение пользователя по запросу
    @GetMapping("/{id}")
    public User findUser(@PathVariable int id){
        return userService.get(id);
    }

    //Создание пользователя;
    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    //Обновление пользователя;
    @PutMapping
    public User update(@RequestBody @Valid User user) {
        return userService.update(user);
    }

    //Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id,friendId);
    }

    //Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void delFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.delFriend(id, friendId);
    }

    //Возвращаем список пользователей, являющихся его друзьями
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id){
        return userService.getFriends(id);
    }

    //Возвращаем список друзей, общих с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId){
        return userService.getCommonFriends(id, otherId);
    }
}
