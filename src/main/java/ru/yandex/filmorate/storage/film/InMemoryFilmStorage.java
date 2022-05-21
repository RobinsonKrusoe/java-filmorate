package ru.yandex.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.filmorate.exceptions.ValidationException;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.storage.InMemoryItemStorage;

import java.time.LocalDate;

//Класс для реализации интерфейса для работы с хранилищем Фильмов
@Component
public class InMemoryFilmStorage extends InMemoryItemStorage<Film> implements FilmStorage {
    protected static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private static final LocalDate FIRST_POSSIBLE_RELEASE = LocalDate.of(1895, 12, 28);

    //Получение пользователя
    @Override
    public Film get(Integer id){
        if(items.containsKey(id))
            return items.get(id);
        else{
            log.error("Фильм #" + id + " не найден!");
            throw new FilmNotFoundException("Фильм #" + id + " не найден!");
        }
    }

    //добавление фильма
    @Override
    public Film create(Film film) {
        validateFilm(film);
        //Проверка занятости идентификатора
        if (items.containsKey(film.getId())) {
            log.error("Фильм с номером #" + film.getId() + " уже существует!");
            throw new FilmAlreadyExistException("Фильм с номером #" + film.getId() + " уже существует!");
        }

        //Проверка существования в списке фильма с аналогичным именем (с учётом года выпуска)
        for (Film filmInFor : items.values()){
            if(filmInFor.getName().equals(film.getName()) &&
                    (filmInFor.getReleaseDate() == null && film.getReleaseDate() == null ||
                            filmInFor.getReleaseDate() != null && filmInFor.getReleaseDate().equals(film.getReleaseDate()))) {
                log.error("Фильм с названием \"" + filmInFor.getName() +
                        "\" с датой выпуска " + filmInFor.getReleaseDate() + " уже существует!");
                throw new FilmAlreadyExistException("Фильм с названием \"" + filmInFor.getName() +
                        "\" с датой выпуска " + filmInFor.getReleaseDate() + " уже существует!");
            }
        }

        //Если номер фильма не задан вручную - сгенерировать его автоматически
        if (film.getId() <= 0)
            film.setId(calcNewNum());

        items.put(film.getId(), film);    //Вставить фильм в список
        log.info("Добавлен новый фильм: " + film);

        return film;
    }

    //обновление фильма
    @Override
    public Film update(Film film) {
        validateFilm(film);
        if(items.containsKey(film.getId())) {
            items.replace(film.getId(), film);
            log.info("Изменён фильм: " + film);
            return film;
        } else {
            log.error("Фильм с идентификатором " + film.getId() + " не найден!");
            throw new FilmNotFoundException("Фильм с идентификатором " + film.getId() + " не найден!");
        }
    }

    //Удаление фильма
    @Override
    public void delete(Integer id){
        if(items.containsKey(id))
            items.remove(id);
        else{
            log.error("Фильм #" + id + " не найден!");
            throw new FilmNotFoundException("Фильм #" + id + " не найден!");
        }
    }

    //Дополнительная общая валидация
    private void validateFilm(Film film){
        if (FIRST_POSSIBLE_RELEASE.isAfter(film.getReleaseDate())) {
            log.error("Дата релиза — не может быть раньше 28 декабря 1895 года!");
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года!");
        }
    }
}
