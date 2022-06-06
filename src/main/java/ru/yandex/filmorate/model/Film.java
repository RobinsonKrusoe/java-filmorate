package ru.yandex.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {
    //целочисленный идентификатор
    private int id;

    //название
    @NotBlank(message = "Название фильма не может быть пустым!")
    private String name;

    //описание
    @Size(max = 200, message = "Превышена максимальная длина описания фильма — 200 символов!")
    private String description;

    //дата релиза;
    private LocalDate releaseDate;

    //продолжительность фильма.
    @Positive(message = "Продолжительность фильма должна быть положительной!")
    private int duration;

    //рейтинг Ассоциации кинокомпаний (англ. Motion Picture Association, сокращённо МРА)

    private MPA mpa;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, MPA mpa){
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    //Нравки пользователей
    private Set<Integer> likes;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum MPA{
        G(1, "G"),      //у фильма нет возрастных ограничений,
        PG(2, "PG"),     //детям рекомендуется смотреть фильм с родителями,
        PG_13(3, "PG-13"),  //детям до 13 лет просмотр не желателен,
        R(4, "R"),      //лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
        NC_17(5, "NC-17")   //лицам до 18 лет просмотр запрещён.
        ;
        private final Integer id;
        private final String name;
        MPA(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @JsonProperty("id")
        public Integer getId(){
            return id;
        }

        @JsonProperty("name")
        public String getName(){
            return name;
        }

        @JsonCreator
        public static MPA forValues(@JsonProperty("id") Integer id) {
            for (MPA m : MPA.values()) {
                if (m.id.equals(id))
                    return m;
            }
            return null;
        }
    }
}
