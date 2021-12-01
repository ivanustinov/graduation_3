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
VALUES ('Харчо', now(), 300, 1),
       ('Жаркое', now(), 356.45, 1),
       ('Компот', now(), 30, 1),
       ('Сельдь под шубой', now(), 176.47, 2),
       ('Пиво', now(), 150, 2),
       ('Пиво', '2015-04-16', 280, 2),
       ('Каша гречневая с гуляшом', '2015-04-16', 176.50, 2),
       ('Щи со сметаной', '2015-04-16', 140, 2),
       ('Жаркое', '2015-04-30', 260, 1),
       ('Пюре картофельное с голубцами', '2015-04-30', 225.78, 1),
       ('Кофе с круассаном', '2015-04-30', 100.35, 1);
