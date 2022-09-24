drop table if exists users_cloud;
drop table if exists user_secure;

CREATE TABLE users_cloud
(
    id        bigserial primary key,
    username  varchar(128) unique not null,
    image_url varchar(128)        not null
);

create table user_secure
(
    id            bigserial primary key,
    user_login    varchar(128) unique not null,
    user_password varchar(128)        not null
);

create table if not exists tokens
(
    username varchar(128) primary key,
    token    varchar not null
);