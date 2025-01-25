--liquibase formatted sql
--changeset maciej.bronikowski:1733180862_SPRINGCHAT_createUserTable
--comment: [SPRINGCHAT] create User table
create table "user"
(
    id               uuid primary key,
    auth_resource_id text               unique,
    created_at       timestamp not null,
    email            text      not null unique,
    register_email   text      not null,
    type             text      not null,
    email_verified   boolean   not null
);

create table member
(
    id                   uuid primary key references "user"
);
