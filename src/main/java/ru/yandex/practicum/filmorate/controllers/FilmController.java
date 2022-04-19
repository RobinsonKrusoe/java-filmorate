package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate FIRST_POSSIBLE_RELEASE = LocalDate.of(1895, 12, 28);
    private Map<Integer, Film> films = new HashMap<>();

    //добавление фильма
    @PostMapping
    public void create(@RequestBody @Valid  Film film) {
        validateFilm(film);
        //Проверка занятости идентификатора
        if (films.containsKey(film.getId())) {
            log.error("Фильм с номером #" + film.getId() + " уже существует!");
            throw new ValidationException("Фильм с номером #" + film.getId() + " уже существует!");
        }

        //Проверка существования в списке фильма с аналогичным именем (с учётом года выпуска)
        for (Film filmInFor : films.values()){
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

        films.put(film.getId(), film);    //Вставить фильм в список
        log.info("Добавлен новый фильм: " + film);
    }

    //обновление фильма
    @PutMapping
    public void update(@RequestBody @Valid Film film) {
        validateFilm(film);
        if(films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
            log.info("Изменён фильм: " + film);
        } else {
            log.error("Фильм с идентификатором " + film.getId() + " не найден!");
            throw new ValidationException("Фильм с идентификатором " + film.getId() + " не найден!");
        }
    }
    //получение всех фильмов
    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    private void validateFilm(Film film){
        if (FIRST_POSSIBLE_RELEASE.isAfter(film.getReleaseDate())) {
            log.error("Дата релиза — не может быть раньше 28 декабря 1895 года!");
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года!");
        }
    }

    //Формирование идентификатора
    public int calcNewNum(){
        int result = 0;
        //Поиск первого незанятого идентификатора
        for (int i = 1; i <= (films.size() + 1); i++) {
            if (!films.containsKey(i)){
                result = i;
                break;
            }
        }
        return result;
    }
}
