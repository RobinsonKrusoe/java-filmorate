package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

//Класс пользователя
@Data
@AllArgsConstructor
public class User {
    //целочисленный идентификатор
    private int id;

    //электронная почта
    @NotBlank(message = "Электронная почта не может быть пустой!")
    @Email(message = "Некорректный Email!")
    private final String email;

    //логин пользователя
    @NotBlank(message = "Логин не может быть пустым!")
    @Pattern(regexp = "[^\\s]*", message = "Логин не может содержать пробелы!")
    private String login;

    //имя для отображения
    private String displayName;

    //дата рождения
    @PastOrPresent(message = "Дата рождения не может быть в будущем!")
    private LocalDate birthday;

    public String getDisplayName(){
        //Имя для отображения может быть пустым — в таком случае будет использован логин
        return (displayName == null || displayName.isEmpty()) ? login : displayName;
    }
}
