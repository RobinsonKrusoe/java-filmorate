package ru.yandex.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.storage.film.FilmStorage;
import ru.yandex.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

//Класс для операций над фильмами
@Service
public class FilmService {
    private UserStorage userStorage;
    private FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage){
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    //Возвращение всех пользователей
    public List<Film> findAll(){
        return filmStorage.findAll();
    }

    //Возвращение фильма по запросу
    public Film findFilm(Integer id){
        return filmStorage.get(id);
    }

    //Добавление фильма
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    //Обновление фильма
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    //Добавление лайка фильму
    public void addLike(Integer filmid, Integer userId){
        Film film = filmStorage.get(filmid);
        User user = userStorage.get(userId);

        if (film != null && user != null){
            if(film.getLikes() == null) film.setLikes(new HashSet<>());
            film.getLikes().add(user.getId());
        }
    }

    //Удаление лайка у фильма
    public void delLike(Integer filmid, Integer userId){
        Film film = filmStorage.get(filmid);
        User user = userStorage.get(userId);

        if (film != null && user != null && film.getLikes().contains(user.getId())){
            film.getLikes().remove(user.getId());
        }
    }

    //Получение списка наиболее популярных фильмов
    public List<Film> getMostPopular(Integer count){
        List<Film> films = filmStorage.findAll();
        return films.stream()
                .sorted((film1, film2) -> Integer.compare((film2 == null ||
                                                           film2.getLikes() == null) ? 0 : film2.getLikes().size(),
                                                          (film1 == null ||
                                                           film1.getLikes() == null) ? 0 : film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());

    }
}
