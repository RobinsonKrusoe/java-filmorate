package ru.yandex.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Класс рейтинга
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {
    private int id;
    private String name;
    public Mpa(int id){
        this.id = id;
    }
}
