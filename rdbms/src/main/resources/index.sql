--liquibase formatted sql

--changeset rsbryanskiy:index

CREATE INDEX id_version_index ON account1 (id, version);