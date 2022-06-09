package ru.yandex.filmorate.dao;

import java.util.List;

//Интерфейс для работы с простыми сущностями с искусственным ключём
public interface EntityDao<T>{
    //Получение сущности по идентификатору
    T get(Integer id);

    //Добавление сущности
    T create(T item);

    //Удаление сущности
    void delete(Integer id);

    //Обновление сущности
    T update(T item);

    //Список всех сущностей
    List<T> getAll();
}
