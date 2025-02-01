--liquibase formatted sql
--changeset maciej.bronikowski:1738380412_SPRINGCHAT_createChannelFileTable
--comment: [SPRINGCHAT] create ChannelFileTable

create table channel_file(
    id                 uuid primary key,
    created_at         timestamp not null,
    type               text      not null,
    author_id          uuid      not null references member,
    channel_id         uuid      not null references channel,
    file_id            uuid      not null references storage_file,
    channel_message_id uuid references channel_message
);
