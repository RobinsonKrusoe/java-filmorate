package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/films")
public class FilmController extends ItemController<Film>{
    private static final LocalDate FIRST_POSSIBLE_RELEASE = LocalDate.of(1895, 12, 28);

    //добавление фильма
    @PostMapping
    public void create(@RequestBody @Valid  Film film) {
        validateFilm(film);
        //Проверка занятости идентификатора
        if (items.containsKey(film.getId())) {
            log.error("Фильм с номером #" + film.getId() + " уже существует!");
            throw new ValidationException("Фильм с номером #" + film.getId() + " уже существует!");
        }

        //Проверка существования в списке фильма с аналогичным именем (с учётом года выпуска)
        for (Film filmInFor : items.values()){
            if(filmInFor.getName().equals(film.getName()) &&
                    (filmInFor.getReleaseDate() == null && film.getReleaseDate() == null ||
                    filmInFor.getReleaseDate() != null && filmInFor.getReleaseDate().equals(film.getReleaseDate()))) {
                log.error("Фильм с названием \"" + filmInFor.getName() +
                        "\" с датой выпуска " + filmInFor.getReleaseDate() + " уже существует!");
                throw new ValidationException("Фильм с названием \"" + filmInFor.getName() +
                        "\" с датой выпуска " + filmInFor.getReleaseDate() + " уже существует!");
            }
        }

        //Если номер фильма не задан вручную - сгенерировать его автоматически
        if (film.getId() <= 0)
            film.setId(calcNewNum());

        items.put(film.getId(), film);    //Вставить фильм в список
        log.info("Добавлен новый фильм: " + film);
    }

    //обновление фильма
    @PutMapping
    public void update(@RequestBody @Valid Film film) {
        validateFilm(film);
        if(items.containsKey(film.getId())) {
            items.replace(film.getId(), film);
            log.info("Изменён фильм: " + film);
        } else {
            log.error("Фильм с идентификатором " + film.getId() + " не найден!");
            throw new ValidationException("Фильм с идентификатором " + film.getId() + " не найден!");
        }
    }

    private void validateFilm(Film film){
        if (FIRST_POSSIBLE_RELEASE.isAfter(film.getReleaseDate())) {
            log.error("Дата релиза — не может быть раньше 28 декабря 1895 года!");
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года!");
        }
    }
}
