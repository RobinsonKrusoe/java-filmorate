package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exceptions.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    //создание пользователя;
    @PostMapping
    public void create(@RequestBody @Valid User user) {
        //Проверка занятости идентификатора
        if (users.containsKey(user.getId())) {
            log.error("Пользователь с номером #" + user.getId() + " уже существует!");
            throw new ValidationException("Пользователь с номером #" + user.getId() + " уже существует!");
        }

        for (User userInFor: users.values()){
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

        users.put(user.getId(), user);    //Вставить фильм в список
        log.info("Добавлен новый пользователь: " + user);
    }

    //обновление пользователя;
    @PutMapping
    public void update(@RequestBody @Valid User user) {
        if (users.containsKey(user.getId())) {
            for (User userInFor: users.values()) {
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

            users.replace(user.getId(), user);
            log.info("Изменён пользователь: " + user);
        } else {
            log.error("Пользователь с идентификатором " + user.getId() + " не существует!");
            throw new ValidationException("Пользователь с идентификатором " + user.getId() + " не существует!");
        }
    }

    //получение списка всех пользователей.
    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    //Формирование идентификатора
    public int calcNewNum(){
        int result = 0;
        //Поиск первого незанятого идентификатора
        for (int i = 1; i <= (users.size() + 1); i++) {
            if (!users.containsKey(i)){
                result = i;
                break;
            }
        }
        return result;
    }
}
