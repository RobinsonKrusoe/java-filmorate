package ru.yandex.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

//Класс пользователя
@Data
@AllArgsConstructor
public class User {
    //Целочисленный идентификатор
    private int id;

    //Электронная почта
    @NotBlank(message = "Электронная почта не может быть пустой!")
    @Email(message = "Некорректный Email!")
    private final String email;

    //Логин пользователя
    @NotBlank(message = "Логин не может быть пустым!")
    @Pattern(regexp = "[^\\s]*", message = "Логин не может содержать пробелы!")
    private String login;

    //Имя для отображения
    private String name;

    //Дата рождения
    @PastOrPresent(message = "Дата рождения не может быть в будущем!")
    private LocalDate birthday;

    //Друзья пользователя
    private Set<Integer> friends;

    //Получение имени для отображения
    public String getName(){
        //Имя для отображения может быть пустым — в таком случае будет использован логин
        return (name == null || name.isEmpty()) ? login : name;
    }
}
