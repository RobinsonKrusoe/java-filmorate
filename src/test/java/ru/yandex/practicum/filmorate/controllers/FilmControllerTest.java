package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.util.TestClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {
    final String fServURL = "http://localhost:8080/films";
    static TestClient tc;
    static ConfigurableApplicationContext context;

    //Эталонный json фильма
    static final String filmJsonOK = "{" +
            "\"name\":\"Новый фильм\"," +
            "\"description\":\"Валидный пример фильма для вставки\"," +
            "\"releaseDate\":\"1967-03-25\"," +
            "\"duration\":100}";

    //Эталонный json фильма для проверки работы обновления
    static final String filmJsonUpD = "{" +
            "\"id\": 1," +
            "\"name\":\"Название фильма после обновления\"," +
            "\"description\":\"Валидный пример фильма после обновления\"," +
            "\"releaseDate\":\"1967-04-24\"," +
            "\"duration\":100}";

    //Эталонный json фильма для проверки работы обновления несуществующего идентификатора
    static final String filmJsonUpD1 = "{" +
            "\"id\": 100," +
            "\"name\":\"Просто фильм\"," +
            "\"description\":\"Фильм для обновления несуществующего идентификатора\"," +
            "\"releaseDate\":\"1967-04-24\"," +
            "\"duration\":100}";

    //Эталонный json фильма с пустым названием
    static final String filmJsonWithEmptyName = "{" +
            "\"description\":\"Фильм с пустым названием\"," +
            "\"releaseDate\":\"1967-04-24\"," +
            "\"duration\":100}";

    //Эталонный json фильма со слишком длинным описанием
    static final String filmJsonWithTooBigDesc = "{" +
            "\"name\":\"Ещё один фильм\"," +
            "\"description\":\"Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. " +
            "Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. " +
            "Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. " +
            "Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. " +
            "Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. " +
            "Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. Фильм со слишком длинным описанием. \"," +
            "\"releaseDate\":\"1967-04-24\"," +
            "\"duration\":100}";

    //Эталонный json фильма с отрицательной продолжительностью
    static final String filmJsonWithNegativeDuration = "{" +
            "\"name\":\"Ещё один фильм\"," +
            "\"description\":\"Фильм с отрицательной продолжительностью\"," +
            "\"releaseDate\":\"1967-03-25\"," +
            "\"duration\":-100}";

    //Эталонный json фильма со слишком ранней датой выпуска для проверки вставки
    static final String filmJsonWithTooOldRelease = "{" +
            "\"name\":\"Ещё один фильм\"," +
            "\"description\":\"Фильм со слишком ранней датой выпуска\"," +
            "\"releaseDate\":\"1895-12-27\"," +
            "\"duration\":100}";

    //Эталонный json фильма со слишком ранней датой выпуска для проверки обновления
    static final String filmJsonWithTooOldReleaseUpD = "{" +
            "\"id\": 1," +
            "\"name\":\"Фильм для проверки обновления\"," +
            "\"description\":\"Фильм со слишком ранней датой выпуска\"," +
            "\"releaseDate\":\"1895-12-27\"," +
            "\"duration\":100}";

    //Эталонный json со списком из двух фильмов
    static final String twoFilmLisyJson =
            "[{\"id\":1," +
                "\"name\":\"Новый фильм\"," +
                "\"description\":\"Валидный пример фильма для вставки\"," +
                "\"releaseDate\":\"1967-03-25\"," +
                "\"duration\":100}," +
             "{\"id\":100," +
                "\"name\":\"Просто фильм\"," +
                "\"description\":\"Фильм для обновления несуществующего идентификатора\"," +
                "\"releaseDate\":\"1967-04-24\"," +
                "\"duration\":100}]";

    @BeforeAll
    public static void beforeAll() {
        tc = new TestClient();
    }

    @BeforeEach
    public void beforeEach(){
        context = SpringApplication.run(FilmorateApplication.class);
    }

    @AfterEach
    public void afterEach(){
        context.close();
    }

    @Test
    void create() {
        //Создание валидной записи
        tc.post(fServURL, filmJsonOK);
        assertEquals(200, tc.getLastResponseStatusCode(),"Проблема с созданием записи фильма!");

        //Попытка вставки записи с уже существующим идентификатором
        tc.post(fServURL, filmJsonUpD);
        assertEquals(500, tc.getLastResponseStatusCode(),"Ожидался другой код ответа!");

        //Попытка вставки уже существующего фильма (совпадение названия и даты выхода)
        tc.post(fServURL, filmJsonOK);
        assertEquals(500, tc.getLastResponseStatusCode(),"Ожидался другой код ответа!");

        //Попытка вставки фильма с пустым названием
        tc.post(fServURL, filmJsonWithEmptyName);
        assertEquals(400, tc.getLastResponseStatusCode(),"Ожидался другой код ответа!");

        //Попытка вставки фильма с очень длинным описанием
        tc.post(fServURL, filmJsonWithTooBigDesc);
        assertEquals(400, tc.getLastResponseStatusCode(),"Ожидался другой код ответа!");

        //Попытка вставки фильма со слишком ранней датой выпуска
        tc.post(fServURL, filmJsonWithNegativeDuration);
        assertEquals(400, tc.getLastResponseStatusCode(),"Ожидался другой код ответа!");

        //Попытка вставки фильма с отрицательной продолжительностью
        tc.post(fServURL, filmJsonWithTooOldRelease);
        assertEquals(500, tc.getLastResponseStatusCode(),"Ожидался другой код ответа!");
    }

    @Test
    void update() {
        //Создание валидной записи
        tc.post(fServURL, filmJsonOK);
        assertEquals(200, tc.getLastResponseStatusCode(),"Проблема с созданием записи фильма!");

        //Попытка обновления с корректными параметрами
        tc.put(fServURL, filmJsonUpD);
        assertEquals(200, tc.getLastResponseStatusCode(),"Проблема с обновлением записи фильма!");

        //Попытка обновления отсутствующего фильма
        tc.put(fServURL, filmJsonUpD1);
        assertEquals(500, tc.getLastResponseStatusCode(),"Проблема с обновлением записи фильма!");

        //Попытка обновления фильма на слишком раннюю дату выпуска
        tc.put(fServURL, filmJsonWithTooOldReleaseUpD);
        assertEquals(500, tc.getLastResponseStatusCode(),"Проблема с обновлением записи фильма!");
    }

    @Test
    void findAll() {
        //Проверка получения пустого списка
        assertEquals("[]", tc.get(fServURL),"Ожидался пустой список!");

        //Создание валидной записи
        tc.post(fServURL, filmJsonOK);
        assertEquals(200, tc.getLastResponseStatusCode(),"Проблема с созданием записи фильма!");

        //Создание валидной записи
        tc.post(fServURL, filmJsonUpD1);
        assertEquals(200, tc.getLastResponseStatusCode(),"Проблема с созданием записи фильма!");

        //Проверка полученного списка
        assertEquals(twoFilmLisyJson, tc.get(fServURL),
                "Возвращённый список фильмов не совпал с эталонным!");
    }
}