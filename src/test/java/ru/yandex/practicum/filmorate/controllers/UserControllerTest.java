package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.util.TestClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    static TestClient tc;
    static ConfigurableApplicationContext context;
    final String uServURL = "http://localhost:8080/users";

    //Эталонный json пользователя
    static final String userJson = "{" +
            "\"login\":\"correct_login\"," +
            "\"name\":\"Пользователь\"," +
            "\"email\":\"mail@mail.ru\"," +
            "\"birthday\":\"1946-08-20\"," +
            "\"description\":\"Пример корректного пользователя\"}";

    //Эталонный json пользователя
    static final String userJsonTwo = "{" +
            "\"id\":100," +
            "\"login\":\"ok_login\"," +
            "\"name\":\"Ещё один корректный пользователь\"," +
            "\"email\":\"ok_login@mail.ru\"," +
            "\"birthday\":\"1946-08-20\"," +
            "\"description\":\"Пример корректного пользователя\"}";

    //json пользователя для обновления
    static final String userJsonForUpd = "{" +
            "\"id\":1," +
            "\"login\":\"login_correct\"," +
            "\"name\":\"Пользователь после обновления\"," +
            "\"email\":\"liam@mail.ru\"," +
            "\"birthday\":\"1946-08-21\"," +
            "\"description\":\"Корректные данные для обновления пользователя #1\"}";

    //json пользователя с совпадаюим email
    static final String userJsonDublEmail = "{" +
            "\"login\":\"test1\"," +
            "\"name\":\"Пользователь с совпадающим email\"," +
            "\"email\":\"mail@mail.ru\"," +
            "\"birthday\":\"1946-08-20\"," +
            "\"description\":\"Пользователь для проверки дублирования email\"}";

    //json пользователя с совпадающим логином
    static final String userJsonDublLogin = "{" +
            "\"login\":\"correct_login\"," +
            "\"name\":\"Пользовател с совпадающим логином\"," +
            "\"email\":\"mail_1@mail.ru\"," +
            "\"birthday\":\"1946-08-20\"," +
            "\"description\":\"Пользователь для проверки дублирования логина\"}";

    //json пользователя с пустым email
    static final String userJsonEmptyEmail = "{" +
            "\"login\":\"test2\"," +
            "\"name\":\"Пользователь с пустым email\"," +
            "\"birthday\":\"1946-08-20\"," +
            "\"description\":\"Пользовательдля проверки создания пользователя с пустым email\"}";

    //json пользователя с некорректным email
    static final String userJsonBadEmail = "{" +
            "\"login\":\"test3\"," +
            "\"name\":\"Пользователь с некорректным email\"," +
            "\"email\":\"mail(a)mail.ru\"," +
            "\"birthday\":\"1946-08-20\"," +
            "\"description\":\"Пользовательдля проверки некорректного email\"}";

    //json пользователя с пустым логином
    static final String userJsonEmptyLogin = "{" +
            "\"name\":\"Пользовател с пустым логином\"," +
            "\"email\":\"mail_2@mail.ru\"," +
            "\"birthday\":\"1946-08-20\"," +
            "\"description\":\"Пользователь для проверки пустого логина\"}";

    //json пользователя с логином с пробелом
    static final String userJsonLoginWithBlank = "{" +
            "\"login\":\"login with_blank\"," +
            "\"name\":\"Пользовател с логином с пробелом\"," +
            "\"email\":\"mail_3@mail.ru\"," +
            "\"birthday\":\"1946-08-20\"," +
            "\"description\":\"Пользователь для проверки логина с пробелом\"}";

    //json пользователя с днём рождения в будущем
    static final String userJsonOfFutureMan = "{" +
            "\"login\":\"test4\"," +
            "\"name\":\"Пользователь с днём рождения в будущем\"," +
            "\"email\":\"mail_4@mail.ru\"," +
            "\"birthday\":\"2146-08-20\"," +
            "\"description\":\"Пользовательдля проверки пользователя с днём рождения в будущем\"}";

    static final String twoUserListJson =
        "[{\"id\":1," +
          "\"email\":\"mail@mail.ru\"," +
          "\"login\":\"correct_login\","+
          "\"displayName\":\"correct_login\","+
          "\"birthday\":\"1946-08-20\"},"+
         "{\"id\":100,"+
          "\"email\":\"ok_login@mail.ru\","+
          "\"login\":\"ok_login\","+
          "\"displayName\":\"ok_login\","+
          "\"birthday\":\"1946-08-20\"}]";

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
        tc.post(uServURL, userJson);
        assertEquals(200, tc.getLastResponseStatusCode(),"Проблема с созданием пользователя!");

        //Попытка вставки пользователя с уже существующим идентификатором
        tc.post(uServURL, userJson);
        assertEquals(500, tc.getLastResponseStatusCode(),
                "Ожидался другой код ответа при дублировании идентификатора!");

        //Попытка вставки двух пользователей с одинаковой почтой
        tc.post(uServURL, userJsonDublEmail);
        assertEquals(500, tc.getLastResponseStatusCode(),
                "Ожидался другой код ответа при дублировании Email!");

        //Попытка вставки пользователя с логином, который уже используется
        tc.post(uServURL, userJsonDublLogin);
        assertEquals(500, tc.getLastResponseStatusCode(),
                "Ожидался другой код ответа при дублировании логина!");

        //Попытка вставки пользователя с пустой почтой
        tc.post(uServURL, userJsonEmptyEmail);
        assertEquals(400, tc.getLastResponseStatusCode(),
                "Ожидался другой код ответа при вставке пользователя с пустым Email!");

        //Попытка вставки пользователя с некорректной почтой
        tc.post(uServURL, userJsonBadEmail);
        assertEquals(400, tc.getLastResponseStatusCode(),
                "Ожидался другой код ответа при вставке пользователя с некорректным Email!");

        //Попытка вставки пользователя с пустым лонином
        tc.post(uServURL, userJsonEmptyLogin);
        assertEquals(400, tc.getLastResponseStatusCode(),
                "Ожидался другой код ответа при вставке пользователя с пустым логином!");

        //Попытка вставки пользователя с логином с пробелом
        tc.post(uServURL, userJsonLoginWithBlank);
        assertEquals(400, tc.getLastResponseStatusCode(),
                "Ожидался другой код ответа при вставке пользователя с логином с пробелом!");

        //Попытка вставки пользователя с датой рождения в будущем
        tc.post(uServURL, userJsonOfFutureMan);
        assertEquals(400, tc.getLastResponseStatusCode(),
                "Ожидался другой код ответа при вставке пользователя с датой рождения в будущем!");
    }

    @Test
    void update() {
        //Создание валидной записи
        tc.post(uServURL, userJson);
        assertEquals(200, tc.getLastResponseStatusCode(),"Проблема с созданием пользователя!");

        //Попытка обновления с корректными параметрами
        tc.put(uServURL, userJsonForUpd);
        assertEquals(200, tc.getLastResponseStatusCode(),"Проблема с обновлением пользователя!");

        //Попытка обновления отсутствующего пользователя
        tc.put(uServURL, userJsonTwo);
        assertEquals(500, tc.getLastResponseStatusCode(),
                "Ожидался другой код ответа при обновлении несуществующего пользователя!");
    }

    @Test
    void findAll() {
        //Проверка получения пустого списка
        assertEquals("[]", tc.get(uServURL),"Ожидался пустой список!");

        //Создание валидной записи
        tc.post(uServURL, userJson);
        assertEquals(200, tc.getLastResponseStatusCode(),"Проблема с созданием пользователя!");

        //Создание ещё одной валидной записи
        tc.post(uServURL, userJsonTwo);
        assertEquals(200, tc.getLastResponseStatusCode(),"Проблема с созданием пользователя!");

        //Проверка полученного списка
        assertEquals(twoUserListJson, tc.get(uServURL),
                "Возвращённый список пользователей не совпал с эталонным!");
    }
}