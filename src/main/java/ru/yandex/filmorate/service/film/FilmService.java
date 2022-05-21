package ru.yandex.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

//Класс для операций над фильмами
@Service
public class FilmService {
    //Добавление лайка фильму
    public void addLike(Film film, User user){
        if (film != null && user != null){
            if(film.getLikes() == null) film.setLikes(new HashSet<>());
            film.getLikes().add(user.getId());
        }
    }

    //Удаление лайка у фильма
    public void delLike(Film film, User user){
        if (film != null && user != null && film.getLikes().contains(user.getId())){
            film.getLikes().remove(user.getId());
        }
    }

    //Получение списка наиболее популярных фильмов
    public List<Film> getMostPopular(List<Film> films, Integer count){
        return films.stream()
                .sorted((film1, film2) -> Integer.compare((film2 == null ||
                                                           film2.getLikes() == null) ? 0 : film2.getLikes().size(),
                                                          (film1 == null ||
                                                           film1.getLikes() == null) ? 0 : film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());

    }
}
