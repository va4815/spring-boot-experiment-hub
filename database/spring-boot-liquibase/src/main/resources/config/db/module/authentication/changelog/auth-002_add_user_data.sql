--liquibase formatted sql

--changeset vincent:auth-002

--pre-conditions-table-exists schema_authentication.role
--pre-conditions-table-exists schema_authentication.user_account

INSERT INTO schema_authentication.role (name)
VALUES ('Admin');

INSERT INTO schema_authentication.user_account (role_id, username)
    SELECT id, 'Dev Admin' FROM schema_authentication.role
WHERE name = 'Admin';

