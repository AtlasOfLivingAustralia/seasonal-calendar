#!/usr/bin/env bash
pushd seasonal-calendar-api
cp local.ci local.properties
psql -c 'create database seasonal-calendars;' -U postgres
chmod +x gradlew
./gradlew test
./gradlew build
./gradlew assemble --refresh-dependencies
popd
