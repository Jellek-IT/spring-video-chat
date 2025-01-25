--liquibase formatted sql
--changeset maciej.bronikowski:1737588159_SPRINGCHAT_createShedlockTable
--comment: [SPRINGCHAT] create Shedlock table associated with lukas-krecan/ShedLock plugin

create table shedlock(
    name       varchar(64)  not null primary key,
    lock_until timestamp    not null,
    locked_at  timestamp    not null,
    locked_by  varchar(255) not null
);
