/*
Add as-idempotent-as-possible DDL statements here
See: http://www.jeremyjarrell.com/using-flyway-db-with-distributed-version-control/
*/

INSERT INTO role (name) VALUES ('ROLE_CALENDAR_ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO role (name) VALUES ('ROLE_CALENDAR_EDITOR') ON CONFLICT DO NOTHING;
