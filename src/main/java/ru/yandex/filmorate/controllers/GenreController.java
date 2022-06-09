package ru.yandex.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.filmorate.model.Genre;
import ru.yandex.filmorate.service.genre.GenreService;

import java.util.List;

//Класс контроллера для работы со справочником жанров
@RestController
@RequestMapping("/genres")
public class GenreController {
    private GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService){
        this.genreService = genreService;
    }

    //Возвращение жанра по идентификатору
    @GetMapping("/{id}")
    public Genre findMpa(@PathVariable int id){
        return genreService.get(id);
    }

    //Возвращение жанра по идентификатору
    @GetMapping
    public List<Genre> findAll(){
        return genreService.getAll();
    }
}
