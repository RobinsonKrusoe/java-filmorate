# *Filmorate*
___
### Проект фильмотеки с возможностью оценки фильмов пользователями и объединением пользователей дружескими связями.
___

  Схема данных:

![Схема данных.](/src/main/resources/static/DBDiagram.png?raw=true "Схема данных.")


| Таблица          | Описание                                                            |
|------------------|---------------------------------------------------------------------|
| **_user_**       | Пользователи ресурса                                                |
| **_film_**       | Информация о фильмах                                                |
| **_likes_**      | Таблица связи для хранения лайков пользователей на фильм            |
| **_friends_**    | Таблица связи для хранения информации о дружбе между пользователями |
| **_film_ganre_** | Таблица связи для хранения информации о жанрах фильма               |

Примеры обращения к данным.

Получение списка друзей пользователя p_user:  
````SQL
SELECT *
  FROM user u 
 WHERE u.user_id in (SELECT friend_id 
                       FROM friends f
                      WHERE f.user_id = :p_user
                        AND f.status = 'CONFIRMED');
````

Получение списка пользователей, желающих добавить в друзья пользователя p_user:
````SQL
SELECT *
  FROM user u 
 WHERE u.user_id in (SELECT friend_id 
                       FROM friends f
                      WHERE f.user_id = :p_user
                        AND f.status = 'REQUEST');
````

Получение жанров фильмов, которые нравятся пользователю p_user:  
````SQL
SELECT DISTINCT g.hame ganre_name
  FROM film f
  LEFT JOIN film_ganre fg ON (f.film_id = fg.film_id) 
  LEFT JOIN ganre g ON (fg.ganre_id = g.ganre_id)
 WHERE f.film_id in (SELECT friend_id
                       FROM likes l
                      WHERE l.user_id = :p_user);
````

Получение общих друзей пользователей p_user_1 и p_user_2:
````SQL
SELECT *
  FROM user u
 WHERE u.user_id in (SELECT friend_id
                       FROM friends f
                      WHERE f.user_id = :p_user_1
                        AND f.status = 'CONFIRMED')
   AND u.user_id in (SELECT friend_id
                     FROM friends f
                     WHERE f.user_id = :p_user_2
                       AND f.status = 'CONFIRMED');
````
