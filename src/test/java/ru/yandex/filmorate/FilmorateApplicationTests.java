package ru.yandex.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.filmorate.exceptions.UserNotFoundException;
import ru.yandex.filmorate.model.Film;
import ru.yandex.filmorate.model.Mpa;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.storage.film.FilmDbStorage;
import ru.yandex.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


//Класс для тестирования непосредственно сервисов приложения
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final @Qualifier("UserDbStorage") UserDbStorage userStorage;
    private final @Qualifier("FilmDbStorage") FilmDbStorage filmStorage;

    private static User user;
    private static User user1;
    private static Film film;
    private static Film film1;

    @BeforeAll
    public static void beforeAll() {
        //Экземпляр пользователя для вставки
        user = new User();
        user.setLogin("correct_login");
        user.setEmail("mail@mail.ru");
        user.setName("Пользователь");
        user.setBirthday(LocalDate.parse("1946-08-20"));

        //Экземпляр пользователя для обновления
        user1 = new User();
        user1.setId(1);
        user1.setLogin("login_correct");
        user1.setEmail("liam@mail.ru");
        user1.setName("Пользователь после обновления");
        user1.setBirthday(LocalDate.parse("1946-08-21"));

        //Экземпляр фильма для вставки
        film = new Film();
        film.setName("Новый фильм");
        film.setDescription("Валидный пример фильма для вставки");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(100);
        film.setMpa(new Mpa(4, "G"));

        //Экземпляр фильма для обновления
        film1 = new Film();
        film1.setId(1);
        film1.setName("Название фильма после обновления");
        film1.setDescription("Валидный пример фильма после обновления");
        film1.setReleaseDate(LocalDate.parse("1967-04-24"));
        film1.setDuration(120);
        film1.setMpa(new Mpa(4, "R"));
    }

    /* ------------ Пользователи ------------- */
    //Получение пользователя
    @Test
    public void testGetUserById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.get(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    //Добавление пользователя
    @Test
    public void testAddUser() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.create(user));
        assertThat(userOptional).isPresent();

        //Отдельный запрос нового пользователя
        Optional<User> userRetOptional = Optional.ofNullable(userStorage.get(userOptional.get().getId()));

        assertThat(userRetOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", user.getEmail()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", user.getLogin()));

        //Удаление нового пользователя
        userStorage.delete(userOptional.get().getId());
    }

    //Удаление пользователя
    @Test
    public void testDeleteUser() {
        //Создание пользователя для последующего удаления
        User newUser = userStorage.create(user);

        //Удаление пользователя
        userStorage.delete(newUser.getId());
        assertThrows(UserNotFoundException.class,
                () -> userStorage.get(newUser.getId()),
                "Ожидалось исключение при обращении к удалённому пользователю!");
    }

    //Обновление пользователя
    @Test
    public void testUpdateUser() {
        assertDoesNotThrow(()->(userStorage.update(user1)), "Обновление пользователя вызвало исключение!");

        //Запрос пользователя после обновления
        Optional<User> userRetOptional = Optional.ofNullable(userStorage.get(user1.getId()));

        assertThat(userRetOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", user1.getEmail()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", user1.getLogin()));
    }

    //Список пользователей
    @Test
    public void testGetAllUsers() {
        List<User> userList = userStorage.findAll();
        assertEquals(3, userList.size(),"Количество пользователей не совпало с ожидаемым!");
    }

    //Получение друзей пользователя
    @Test
    public void testGetUserFriends() {
        List<User> userList = userStorage.getFriends(1);
        assertEquals(1, userList.size(),"Количество друзей пользователя не совпало с ожидаемым!");
    }

    //Добавление в друзья
    @Test
    public void testAddFriend() {
        userStorage.addFriend(1, 2);
        List<User> userList = userStorage.getFriends(1);

        assertEquals(2, userList.size(),
                "Количество друзей пользователя после добавления не совпал с ожидаемым!");

        userStorage.delFriend(1, 2);
    }

    //Удаление из друзей
    @Test
    public void testDelFriend() {
        userStorage.delFriend(2, 3);
        List<User> userList = userStorage.getFriends(2);

        assertEquals(0, userList.size(),
                "Количество друзей пользователя после удаления не совпал с ожидаемым!");

        userStorage.addFriend(2, 3);
    }

    //Получение списка общих друзей
    @Test
    public void testGetCommonFriends() {
        assertEquals(1, userStorage.getCommonFriends(1, 2).size(),
                "Количество общих друзей пользователей не совпало с ожидаемым!");
    }

    /* ------------ Фильмы ------------- */

    //Получение фильма
    @Test
    public void testGetFilm() {
        Optional<Film> userOptional = Optional.ofNullable(filmStorage.get(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    //Добавление фильма
    @Test
    public void testAddFilm() {
        assertDoesNotThrow(()->(filmStorage.create(film)), "Создание фильма вызвало исключение!");
        filmStorage.delete(3);
    }

    //Удаление фильма
    @Test
    public void testDeleteFilm() {
        filmStorage.delete(2);
        assertEquals(1, filmStorage.findAll().size(),
                "Количество фильмов после удаления не совпало с ожидаемым!");
    }

    //Обновление фильма
    @Test
    public void testUpdateFilm() {
        assertDoesNotThrow(()->(filmStorage.update(film1)), "Обновление фильма вызвало исключение!");
    }

    //Список всех фильмов
    @Test
    public void testGetAllFilms() {
        List<Film> userList = filmStorage.findAll();
        assertEquals(2, userList.size(),"Количество фильмов не совпало с ожидаемым!");
    }

    //Добавление лайка фильму
    @Test
    public void testAddLike() {
        filmStorage.addLike(filmStorage.get(1), userStorage.get(1));
    }

    //Удаление лайка у фильма
    @Test
    public void testDeleteLike() {
        filmStorage.delLike(filmStorage.get(1), userStorage.get(1));
    }

    //Получение списка наиболее популярных фильмов
    @Test
    public void testGetMostPopularFilms() {
        List<Film> userList = filmStorage.getMostPopular(10);
        assertEquals(2, userList.size(),"Количество фильмов в выборке не совпало с ожидаемым!");
    }
}
