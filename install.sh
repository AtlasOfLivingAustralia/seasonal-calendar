#!/usr/bin/env bash
pushd seasonal-calendar-api
./gradlew test
./gradlew build
popd
