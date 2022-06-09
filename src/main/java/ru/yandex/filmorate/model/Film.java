package ru.yandex.filmorate.model;

import lombok.NoArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class Film {
    //целочисленный идентификатор
    private int id;

    //название
    @NotBlank(message = "Название фильма не может быть пустым!")
    private String name;

    //описание
    @Size(max = 200, message = "Превышена максимальная длина описания фильма — 200 символов!")
    private String description;

    //дата релиза;
    private LocalDate releaseDate;

    //продолжительность фильма.
    @Positive(message = "Продолжительность фильма должна быть положительной!")
    private int duration;

    //рейтинг Ассоциации кинокомпаний (англ. Motion Picture Association, сокращённо МРА)
    private Mpa mpa;

    //Жанры фильма
    private List<Genre> genres;

    //Нравки пользователей
    private List<Integer> likes;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa){
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }


}
