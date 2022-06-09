MERGE INTO genre KEY (genre_id) VALUES (1, 'Комедия');
MERGE INTO genre KEY (genre_id) VALUES (2, 'Драма');
MERGE INTO genre KEY (genre_id) VALUES (3, 'Мультфильм');
MERGE INTO genre KEY (genre_id) VALUES (4, 'Триллер');
MERGE INTO genre KEY (genre_id) VALUES (5, 'Документальный');
MERGE INTO genre KEY (genre_id) VALUES (6, 'Боевик');

INSERT INTO "user" (email, login, name, birthday) VALUES ('dolore@mail.ru', 'dolore', 'Nick Name', '1946-08-20');
INSERT INTO "user" (email, login, name, birthday) VALUES ('friend@mail.ru', 'friend', 'friend adipisicing', '1976-08-20');
INSERT INTO "user" (email, login, name, birthday) VALUES ('common_friend@mail.ru', 'common_friend', 'common friend', '1970-08-20');

INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES ('labore nulla', 'Duis in consequat esse', '1979-04-17', 100, 1);
INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES ('New film', 'New film about friends', '1999-04-30', 120, 4);

INSERT INTO friends VALUES (1, 3);
INSERT INTO friends VALUES (2, 3);