package ru.yandex.filmorate.storage.film;

import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.model.User;

import java.util.List;

//Интерфейс для работы с хранилищем Фильмов
public interface FilmStorage {
    //Получение фильма
    Film get(Integer id);

    //Добавление фильма
    Film create(Film film);

    //Удаление фильма
    void delete(Integer id);

    //Обновление фильма
    Film update(Film film);

    //Список всех фильмов
    List<Film> findAll();

    //Добавление лайка фильму
    void addLike(Film film, User user);

    //Удаление лайка у фильма
    void delLike(Film film, User user);

    //Получение списка наиболее популярных фильмов
    List<Film> getMostPopular(Integer count);
}
