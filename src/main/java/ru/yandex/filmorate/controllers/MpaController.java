package ru.yandex.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.filmorate.model.Mpa;
import ru.yandex.filmorate.service.mpa.MpaService;

import java.util.List;

//Класс контроллера для работы со справочником рейтингов фильмов
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService){
        this.mpaService = mpaService;
    }

    //Возвращение рейтинга по идентификатору
    @GetMapping("/{id}")
    public Mpa findMpa(@PathVariable int id){
        return mpaService.get(id);
    }

    //Возвращение рейтинга по идентификатору
    @GetMapping
    public List<Mpa> findAll(){
        return mpaService.getAll();
    }
}
