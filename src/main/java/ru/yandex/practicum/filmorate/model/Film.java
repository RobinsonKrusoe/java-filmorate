package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    //целочисленный идентификатор
    private int id;

    //название
    @NotBlank(message = "Название фильма не может быть пустым!")
    private final String name;

    //описание
    @Size(max = 200, message = "Превышена максимальная длина описания фильма — 200 символов!")
    private String description;

    //дата релиза;
    private LocalDate releaseDate;

    //продолжительность фильма.
    @Positive(message = "Продолжительность фильма должна быть положительной!")
    private int duration;
}
