package ru.yandex.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Класс связи дружбы пользователей
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    private int userId;
    private int friendId;
}
