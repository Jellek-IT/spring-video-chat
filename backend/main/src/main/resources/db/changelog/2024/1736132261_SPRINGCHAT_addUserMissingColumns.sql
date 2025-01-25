--liquibase formatted sql
--changeset maciej.bronikowski:1736132261_SPRINGCHAT_addUserMissingColumns
--comment: [SPRINGCHAT] add User missing columns
alter table "user"
    add column profile_picture_id uuid unique references storage_file;

alter table member
    add column nickname text not null;