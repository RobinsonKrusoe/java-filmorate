package ru.yandex.filmorate.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public abstract class InMemoryItemStorage<T> {
    //хранилище элементов
    protected Map<Integer, T> items = new HashMap<>();

    //Выдача списка всех элементов
    public List<T> findAll() {
        return new ArrayList<>(items.values());
    }

    //Формирование идентификатора
    public int calcNewNum(){
        int result = 0;
        //Поиск первого незанятого идентификатора
        for (int i = 1; i <= (items.size() + 1); i++) {
            if (!items.containsKey(i)){
                result = i;
                break;
            }
        }
        return result;
    }
}
