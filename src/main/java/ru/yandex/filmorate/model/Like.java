package ru.yandex.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Класс лайка фильма
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    private int filmId;
    private int userId;
}
