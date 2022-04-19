package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Абстрактный класс контроллера элементов
public abstract class ItemController<T> {
    //Логер
    protected static final Logger log = LoggerFactory.getLogger(FilmController.class);

    //хранилище элементов
    protected Map<Integer, T> items = new HashMap<>();

    //добавление элемента
    @PostMapping
    public abstract void create(@RequestBody @Valid T item);

    //обновление фильма
    @PutMapping
    public abstract void update(@RequestBody @Valid T item);

    //получение всех фильмов
    @GetMapping
    public List<T> findAll() {
        return new ArrayList<>(items.values());
    }

    //Формирование идентификатора
    public int calcNewNum(){
        int result = 0;
        //Поиск первого незанятого идентификатора
        for (int i = 1; i <= (items.size() + 1); i++) {
            if (!items.containsKey(i)){
                result = i;
                break;
            }
        }
        return result;
    }
}
