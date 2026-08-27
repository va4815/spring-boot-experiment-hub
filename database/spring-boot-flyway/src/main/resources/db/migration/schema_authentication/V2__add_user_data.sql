INSERT INTO schema_authentication.role (name)
VALUES ('Admin');

INSERT INTO schema_authentication.user_account (role_id, username)
SELECT id, 'Dev Admin' FROM schema_authentication.role
WHERE name = 'Admin';