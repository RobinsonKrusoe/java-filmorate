package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exceptions.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController extends ItemController<User>{

    //создание пользователя;
    @PostMapping
    public void create(@RequestBody @Valid User user) {
        //Проверка занятости идентификатора
        if (items.containsKey(user.getId())) {
            log.error("Пользователь с номером #" + user.getId() + " уже существует!");
            throw new ValidationException("Пользователь с номером #" + user.getId() + " уже существует!");
        }

        for (User userInFor: items.values()){
            if(userInFor.getEmail().equals(user.getEmail())){
                log.error("Пользователь с Email" + userInFor.getEmail() + " уже существует!");
                throw new ValidationException("Пользователь с Email " + userInFor.getEmail() + " уже существует!");
            }

            if(userInFor.getLogin().equals(user.getLogin())){
                log.error("Логин " + userInFor.getLogin() + " уже занят!");
                throw new ValidationException("Логин " + userInFor.getLogin() + " уже занят!");
            }
        }

        //Если номер фильма не задан - сгенерировать его автоматически
        if (user.getId() <= 0)
            user.setId(calcNewNum());

        items.put(user.getId(), user);    //Вставить фильм в список
        log.info("Добавлен новый пользователь: " + user);
    }

    //обновление пользователя;
    @PutMapping
    public void update(@RequestBody @Valid User user) {
        if (items.containsKey(user.getId())) {
            for (User userInFor: items.values()) {
                if (user.getId() != userInFor.getId()) {
                    if (userInFor.getEmail().equals(user.getEmail())) {
                        log.error("Пользователь с Email" + userInFor.getEmail() + " уже существует!");
                        throw new ValidationException("Пользователь с Email " + userInFor.getEmail() + " уже существует!");
                    }

                    if (userInFor.getLogin().equals(user.getLogin())) {
                        log.error("Логин " + userInFor.getLogin() + " уже занят!");
                        throw new ValidationException("Логин " + userInFor.getLogin() + " уже занят!");
                    }
                }
            }

            items.replace(user.getId(), user);
            log.info("Изменён пользователь: " + user);
        } else {
            log.error("Пользователь с идентификатором " + user.getId() + " не существует!");
            throw new ValidationException("Пользователь с идентификатором " + user.getId() + " не существует!");
        }
    }
}
