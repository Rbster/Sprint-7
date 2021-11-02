--liquibase formatted sql

--changeset rsbryanskiy:init

create table account1
(
    id bigserial constraint account_pk primary key,
    amount bigint constraint positive_amount CHECK (amount >= 0),
    version bigint
);


