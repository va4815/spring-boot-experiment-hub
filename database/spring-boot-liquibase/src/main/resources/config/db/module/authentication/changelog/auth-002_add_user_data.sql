--liquibase formatted sql

--changeset vincent:auth-002 context:auth-v2

--preconditions onFail:HALT onError:HALT
--precondition-table-exists schema:schema_authentication table:role
--precondition-table-exists schema:schema_authentication table:user_account

--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM schema_authentication.role WHERE name = 'Admin';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM schema_authentication.user_account WHERE username = 'Dev Admin';

INSERT INTO schema_authentication.role (name)
VALUES ('Admin');

INSERT INTO schema_authentication.user_account (role_id, username)
    SELECT id, 'Dev Admin' FROM schema_authentication.role
WHERE name = 'Admin';

--rollback DELETE FROM schema_authentication.user_account WHERE username = 'Dev Admin';
--rollback DELETE FROM schema_authentication.role WHERE name = 'Admin';