--liquibase formatted sql
--changeset maciej.bronikowski:1736686378_SPRINGCHAT_createChannelTables
--comment: [SPRINGCHAT] create Channel tables

create table channel
(
    id               uuid primary key,
    created_at       timestamp not null,
    name             text      not null,
    deleted_at       timestamp,
    thumbnail_id     uuid unique references storage_file
);

create table channel_member
(
    channel_id uuid      not null references channel,
    member_id  uuid      not null references member,
    created_at timestamp not null,
    deleted_at timestamp,
    rights     jsonb     not null,

    primary key(channel_id, member_id),
    unique (channel_id, member_id)
);
