package ru.yandex.filmorate.service.mpa;

import org.springframework.stereotype.Service;
import ru.yandex.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.filmorate.model.Mpa;

import java.util.List;

//Класс для операций со справочником рейтингов
@Service
public class MpaService {
    private MpaDaoImpl mpaStorage;

    //Конструктор сервиса
    public MpaService(MpaDaoImpl mpaStorage){
        this.mpaStorage = mpaStorage;
    }

    //Возвращение рейтинга по идентификатору
    public Mpa get(int id){
        return mpaStorage.get(id);
    }

    //Возвращение списка всех рейтингов
    public List<Mpa> getAll(){
        return mpaStorage.getAll();
    }
}
