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

Run `./new-migration.groovy` then edit the resulting file.

## Tips for authoring a migration

Since backing out a migration can be a pain, try authoring / testing the migration in a transaction first, eg open your favourite SQL tool such as pgadmin, `psql` or the IntelliJ DB tools and:

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

*NOTE:* **Never** edit a migration if it has been applied to a production database.  Probably don't edit a migration if it's been applied to a co-worker's database either.  In these cases just write a follow up migration.

1. Return to the pre-migration state:
    1. If the migration failed, then the migration transaction will have been aborted automatically.
    2. If you have data to keep, undo the migration manually then delete the corresponding row from the `flyway_schema_history` table.  Finally ensure the JOOQ generated code is deleted: `./gradlew clean`
    3. If you just want to start the db from scratch: `./gradlew flywayClean clean`
2. Edit the migration
3. Rerun the migration and jooq codegen: `./gradlew generateSeasonalCalendarJooqSchemaSource`
