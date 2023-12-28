-- Создание схемы
CREATE SCHEMA IF NOT EXISTS voting;

-- Переключение на созданную схему
SET search_path TO voting;

CREATE TABLE IF NOT EXISTS voting.users (
  id    SERIAL PRIMARY KEY,
  name  VARCHAR(255) NOT NULL,
  email varchar(100) not null
      constraint email_constr unique,
  password VARCHAR(255) NOT NULL
);

create table IF NOT EXISTS user_roles (
    user_id integer not null
        constraint user_roles_fk
            references voting.users
            on delete cascade,
    role    varchar(255),
    constraint uk_user_roles
        unique (user_id, role)
);

CREATE TABLE IF NOT EXISTS voting.restaurant (
    id serial primary key,
    name varchar(100) not null
        constraint name_unique_idx unique
);

CREATE TABLE IF NOT EXISTS voting.dish (
     id            serial primary key,
     name          varchar(100)   not null,
     date          date           not null,
     price         numeric(7, 2) not null,
     restaurant_id integer        not null
         constraint dish_rest_constr
             references restaurant
             on delete cascade,
     constraint date_name_rest_unique_idx
         unique (date, name, restaurant_id)
);

CREATE TABLE IF NOT EXISTS voting.vote (
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

alter table vote owner to "user";

alter table dish owner to "user";

alter table restaurant owner to "user";

create index date_name_idx on voting.dish (date, name);


