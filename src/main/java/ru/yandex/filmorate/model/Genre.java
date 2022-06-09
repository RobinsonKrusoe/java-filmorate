package ru.yandex.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Класс жанра фильма
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    private int id;
    private String name;
    public Genre(int id){
        this.id = id;
    }
}
