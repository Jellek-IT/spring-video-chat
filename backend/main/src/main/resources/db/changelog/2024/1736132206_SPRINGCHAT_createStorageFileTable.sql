--liquibase formatted sql
--changeset maciej.bronikowski:1736132206_SPRINGCHAT_createStorageFileTable
--comment: [SPRINGCHAT] create StorageFile table
create table storage_file(
    id               uuid primary key,
    created_at       timestamp not null,
    folder           text      not null,
    name             text      not null
);
