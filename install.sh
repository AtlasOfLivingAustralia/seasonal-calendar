#!/usr/bin/env bash
pushd seasonal-calendar-api
chmod +x gradlew
./gradlew test
./gradlew build
./gradlew assemble --refresh-dependencies
popd