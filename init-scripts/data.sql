-- -- Создание схемы
-- CREATE SCHEMA IF NOT EXISTS voting;
--
-- -- Переключение на созданную схему
-- SET search_path TO voting;

CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    email    varchar(100) not null
        constraint email_constr unique,
    password VARCHAR(255) NOT NULL
);

create table IF NOT EXISTS user_roles
(
    user_id integer not null
        constraint user_roles_fk
            references users
            on delete cascade,
    role    varchar(255),
    constraint uk_user_roles
        unique (user_id, role)
);

CREATE TABLE IF NOT EXISTS restaurant
(
    id   serial primary key,
    name varchar(100) not null
        constraint name_unique_idx unique
);

CREATE TABLE IF NOT EXISTS dish
(
    id            serial primary key,
    name          varchar(100)  not null,
    date          date          not null,
    price         numeric(7, 2) not null,
    restaurant_id integer       not null
        constraint dish_rest_constr
            references restaurant
            on delete cascade,
    constraint date_name_rest_unique_idx
        unique (date, name, restaurant_id)
);

CREATE TABLE IF NOT EXISTS vote
(
    id            serial primary key,
    date          date    not null,
    restaurant_id integer not null
        constraint vote_rest_constr
            references restaurant
            on delete cascade,
    user_id       integer
        constraint vote_user_constr
            references users
            on delete set null,
    constraint date_user_unique_cts
        unique (date, user_id)
);

alter table vote
    owner to "user";

alter table dish
    owner to "user";

alter table restaurant
    owner to "user";

create index date_name_idx on dish (date, name);

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
