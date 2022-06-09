package ru.yandex.filmorate.service.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.exceptions.ValidationException;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.storage.film.FilmStorage;
import ru.yandex.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

//Класс для операций над фильмами
@Service
public class FilmService {
    private final Logger log = LoggerFactory.getLogger(FilmService.class);
    private static final LocalDate FIRST_POSSIBLE_RELEASE = LocalDate.of(1895, 12, 28);

    private UserStorage userStorage;
    private FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage){
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    //Возвращение всех фильмов
    public List<Film> findAll(){
        return filmStorage.findAll();
    }

    //Возвращение фильма по запросу
    public Film findFilm(Integer id){
        return filmStorage.get(id);
    }

    //Добавление фильма
    public Film create(Film film) {
        checkReleaseDate(film);
        return filmStorage.create(film);
    }

    //Обновление фильма
    public Film update(Film film) {
        checkReleaseDate(film);
        return filmStorage.update(film);
    }

    //Добавление лайка фильму
    public void addLike(Integer filmId, Integer userId){
        filmStorage.addLike(filmStorage.get(filmId), userStorage.get(userId));
    }

    //Удаление лайка у фильма
    public void delLike(Integer filmId, Integer userId){
        filmStorage.delLike(filmStorage.get(filmId), userStorage.get(userId));
    }

    //Получение списка наиболее популярных фильмов
    public List<Film> getMostPopular(Integer count){
        return filmStorage.getMostPopular(count);
    }

    //Дополнительная общая валидация
    private void checkReleaseDate(Film film){
        if (FIRST_POSSIBLE_RELEASE.isAfter(film.getReleaseDate())) {
            log.error("Дата релиза — не может быть раньше 28 декабря 1895 года!");
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года!");
        }
    }
}
