package ru.yandex.filmorate.dao;

import java.util.List;

//Интерфейс для работы с простыми сущностями связи
public interface LinkDao<R> {
    //Добавление связи
    void create(Integer entityOneId, Integer entityTwoId);

    //Удаление связи
    void delete(Integer entityOneId, Integer entityTwoId);

    //Приведение списка связей к данному
    void merge(Integer entityOneId, List<R> entityTwoList);
}
