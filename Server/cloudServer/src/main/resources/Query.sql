drop table if exists users_cloud;
drop table if exists user_secure;

CREATE TABLE users_cloud
(
    id         bigserial primary key,
    username  varchar(128) unique NOT NULL,
    image_url varchar(128)        NOT NULL
);

create table user_secure
(
    id            bigserial primary key,
    user_login     varchar(128) unique NOT NULL,
    user_password varchar(128)        not null
);