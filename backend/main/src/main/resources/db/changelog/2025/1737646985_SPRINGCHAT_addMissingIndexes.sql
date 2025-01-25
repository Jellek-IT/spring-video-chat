--liquibase formatted sql
--changeset maciej.bronikowski:1737646985_SPRINGCHAT_addMissingIndexes
--comment: [SPRINGCHAT] create Shedlock table associated with lukas-krecan/ShedLock plugin

--user
create index user__auth_resource_id__idx
    on "user" (auth_resource_id);

--message
create index channel_message__channel_id_sequence__idx
    on channel_message (channel_id, sequence);
