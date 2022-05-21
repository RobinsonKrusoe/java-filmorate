package ru.yandex.filmorate.storage.film;

import ru.yandex.filmorate.model.Film;

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
}
