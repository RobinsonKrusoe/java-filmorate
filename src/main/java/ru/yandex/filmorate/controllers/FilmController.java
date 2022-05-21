package ru.yandex.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.service.film.FilmService;
import ru.yandex.filmorate.storage.film.FilmStorage;
import ru.yandex.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController{
    private FilmService filmService;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage, UserStorage userStorage){
        this.filmService = filmService;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    //Возвращение всех пользователей
    @GetMapping
    public List<Film> findAll(){
        return filmStorage.findAll();
    }

    //Возвращение фильма по запросу
    @GetMapping("/{id}")
    public Film findFilm(@PathVariable int id){
        return filmStorage.get(id);
    }

    //Добавление фильма
    @PostMapping
    public Film create(@RequestBody @Valid  Film film) {
        return filmStorage.create(film);
    }

    //Обновление фильма
    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        return filmStorage.update(film);
    }

    //пользователь ставит лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(filmStorage.get(id), userStorage.get(userId));
    }

    //пользователь удаляет лайк
    @DeleteMapping("{id}/like/{userId}")
    public void delLike(@PathVariable int id, @PathVariable int userId){
        filmService.delLike(filmStorage.get(id), userStorage.get(userId));
    }

    //Возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано - первые 10
    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10", required = false) int count){
        return  filmService.getMostPopular(filmStorage.findAll(), count);
    }
}
