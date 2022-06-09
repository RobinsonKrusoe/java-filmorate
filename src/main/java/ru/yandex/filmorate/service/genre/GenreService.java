package ru.yandex.filmorate.service.genre;

import org.springframework.stereotype.Service;
import ru.yandex.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.filmorate.model.Genre;

import java.util.List;

//Класс для операций со справочником жанров
@Service
public class GenreService {
    private GenreDaoImpl genreStorage;

    //Конструктор сервиса
    public GenreService(GenreDaoImpl genreStorage){
        this.genreStorage = genreStorage;
    }

    //Возвращение рейтинга по идентификатору
    public Genre get(int id){
        return genreStorage.get(id);
    }

    //Возвращение списка всех рейтингов
    public List<Genre> getAll(){
        return genreStorage.getAll();
    }
}
