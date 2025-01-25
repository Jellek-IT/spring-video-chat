--liquibase formatted sql
--changeset maciej.bronikowski:1736909558_SPRINGCHAT_createChannelMessageTable.sql
--comment: [SPRINGCHAT] create ChannelMessage table

create table channel_message
(
    id         uuid primary key,
    created_at timestamp not null,
    text       text      not null,
    channel_id uuid not null references channel,
    member_id uuid not null references member
);
