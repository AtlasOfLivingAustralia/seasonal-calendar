/*
Add as-idempotent-as-possible DDL statements here
See: http://www.jeremyjarrell.com/using-flyway-db-with-distributed-version-control/
*/
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS calendar;
CREATE TABLE calendar(
  collection_uuid uuid,
  website text,
  youtube_id text,
  organisation_name text,
  contributors text[],
  contact_name text,
  keywords text[],
  development text,
  development_reason text,
  "references" text[],
  reference_links text[],
  language text,
  published boolean,

  PRIMARY KEY(collection_uuid)
);

DROP TABLE IF EXISTS season;
CREATE TABLE season(
  id serial PRIMARY KEY,
  collection_uuid uuid NOT NULL REFERENCES calendar ON DELETE CASCADE ON UPDATE CASCADE,
  local_name text NOT NULL UNIQUE,
  alternate_name text,
  months int4range NOT NULL CHECK (months <@ int4range(1,12)),
  weather_icon text,
  description text NOT NULL,

  profile_uuids uuid[] NOT NULL
);

-- -- TODO can this just be a uuid array on season?
-- DROP TABLE IF EXISTS feature;
-- CREATE TABLE feature(
--   collection_uuid uuid NOT NULL,
--   season_name text NOT NULL,
--   profile_uuid uuid NOT NULL,
--
--   PRIMARY KEY(collection_uuid, season_name, profile_uuid),
--   FOREIGN KEY (collection_uuid, season_name) REFERENCES season (collection_uuid, local_name) ON DELETE CASCADE ON UPDATE CASCADE
-- );

DROP TABLE IF EXISTS role;
CREATE TABLE role(
  id SERIAL PRIMARY KEY,
  name text
);

DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role(
  user_id text NOT NULL,
  role_id integer NOT NULL REFERENCES role(id),
  calendar_uuid uuid NOT NULL REFERENCES calendar ON DELETE CASCADE,
  PRIMARY KEY(user_id, role_id, calendar_uuid)
);