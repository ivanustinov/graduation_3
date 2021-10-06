INSERT INTO USERS (name, email, password)
VALUES ('User', 'user@yandex.ru', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin'),
       ('User2', 'user_2@gmail.ru', '{noop}user2');

INSERT INTO USER_ROLES (role, user_id)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2),
       ('USER', 3);


INSERT INTO RESTAURANT (name)
VALUES ('Харбин'),
       ('Си'),
       ('Ханой');

INSERT INTO VOTE (restaurant_id, date, user_id)
VALUES (1, now(), 1),
       (1, now(), 2),
       (2, '2015-04-16', 1),
       (2, '2015-04-30', 2);

INSERT INTO DISH (name, date, price, restaurant_id)
VALUES ('Харчо', now(), 500, 1),
       ('Жаркое', now(), 500, 1),
       ('Компот', now(), 500, 1),
       ('Селедка', now(), 320, 2),
       ('Пиво', now(), 150, 2),
       ('Фрукты', '2015-04-16', 500, 2),
       ('Солянка', '2015-04-30', 260, 1);
