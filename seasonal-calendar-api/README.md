# Seasonal Calendars API

The SC API is a [Dropwizard](https://dropwizard.io) app that integrates the `profile-service` and `calendars` databases and presents them as a 
unified type.

## Running

Run with `./gradlew run --args "server dev-config.yml"` for dev.

1. `./gradlew assemble` to generate an archive in `build/distributions`
2. Then `tar xf seasonal-calendar-api-$VERSION.tar.gz && seasonal-calendar-api-$VERSION/bin/seasonal-calendar`
for prod.

TODO:
1. Fatjar
2. `./gradle publish` for fatjar or dist.tar.gz

## Prerequisites

The SC API requires a `profile-service` instance and a PostgreSQL database, configure connections to both in `config.yml`.

## Adding a new DB migration

Run `./new-migration.groovy` then edit.

## Tips for authoring a migration

Since backing out a migration can be a pain, try authoring / testing the migration in a transaction first, eg open `psql` or the IntelliJ DB tools and:

```
BEGIN;

// some DDL statements
// some DML statements
// finish DDL statements

// DML to inspect data / tables

ROLLBACK;
```

Alternatively, before embarking on a migration, run a pg backup: `pg_dump -Fc seasonal-calendars > db.dump`
Then to restore: `pg_restore -c db.dump`

## Editting a migration

1. If you have data to keep, back out the migration manually (not necessary if it failed) then delete the corresponding row from the `flyway_schema_history` table.  Then ensure the JOOQ generated code is deleted `./gradlew clean`
2. If you want to start from scratch: `./gradlew flywayClean clean`
3. Edit the migration
4. Rerun the migration and jooq codegen: `./gradlew generateSeasonalCalendarJooqSchemaSource`
