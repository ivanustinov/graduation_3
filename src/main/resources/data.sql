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
       (3, '2015-04-16', 2);

INSERT INTO DISH (name, date, price, restaurant_id)
VALUES ('Харчо', now(), 300.23, 1),
       ('Жаркое', now(), 356.45, 1),
       ('Компот', now(), 30.67, 1),
       ('Сельдь под шубой', now(), 176.47, 2),
       ('Пиво', now(), 150.89, 2),
       ('Горбуша с овощами', '2015-04-16', 280, 1),
       ('Суп грибной', '2015-04-16', 165, 1),
       ('Морс', '2015-04-16', 70, 1),
       ('Пиво', '2015-04-16', 280, 2),
       ('Каша гречневая с гуляшом', '2015-04-16', 176.50, 2),
       ('Щи со сметаной', '2015-04-16', 140, 2),
       ('Голубцы с макаронами', '2015-04-16', 196, 3),
       ('Чай с облепихой', '2015-04-16', 84.3, 3),
       ('Рассольник', '2015-04-16', 173, 3);
