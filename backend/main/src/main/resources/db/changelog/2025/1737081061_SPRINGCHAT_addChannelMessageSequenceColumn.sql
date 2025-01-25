--liquibase formatted sql
--changeset maciej.bronikowski:1737081061_SPRINGCHAT_addChannelMessageSequenceColumn
--comment: [SPRINGCHAT] add ChannelMessage sequence column

alter table channel_message
    add column sequence bigint;

create sequence channel_message_sequence_seq
    owned by channel_message.sequence;

alter table channel_message
    alter column sequence set default nextval('channel_message_sequence_seq');

update channel_message
    set sequence = nextval('channel_message_sequence_seq');

alter table channel_message
    alter column sequence set not null;
