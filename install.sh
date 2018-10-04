#!/usr/bin/env bash
pushd seasonal-calendar-api
cp local.ci local.properties
chmod +x gradlew
./gradlew test
./gradlew build
./gradlew assemble --refresh-dependencies
popd
