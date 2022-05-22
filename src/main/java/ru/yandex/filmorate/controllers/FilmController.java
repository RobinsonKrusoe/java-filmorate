package ru.yandex.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController{
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService){
        this.filmService = filmService;
    }

    //Возвращение всех пользователей
    @GetMapping
    public List<Film> findAll(){
        return filmService.findAll();
    }

    //Возвращение фильма по запросу
    @GetMapping("/{id}")
    public Film findFilm(@PathVariable int id){
        return filmService.findFilm(id);
    }

    //Добавление фильма
    @PostMapping
    public Film create(@RequestBody @Valid  Film film) {
        return filmService.create(film);
    }

    //Обновление фильма
    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        return filmService.update(film);
    }

    //пользователь ставит лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    //пользователь удаляет лайк
    @DeleteMapping("{id}/like/{userId}")
    public void delLike(@PathVariable int id, @PathVariable int userId){
        filmService.delLike(id, userId);
    }

    //Возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано - первые 10
    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10", required = false) int count){
        return  filmService.getMostPopular(count);
    }
}
