#!/usr/bin/env bash
pushd seasonal-calendar-api
./gradlew test
./gradlew build
./gradlew assemble --refresh-dependencies
popd
