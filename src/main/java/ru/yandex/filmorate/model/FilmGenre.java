package ru.yandex.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Класс связи жанра с фильмом
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmGenre {
    private int filmId;
    private int genreId;
}
