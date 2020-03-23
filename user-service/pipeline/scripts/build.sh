#!/usr/bin/env bash

set -eu

export GRADLE_USER_HOME=".gradle"

version=$(cat version/tag)

(
cd source
./gradlew -Pversion="$version" \
    user-service:clean \
    user-service:build \
    --rerun-tasks \
    --no-daemon \
    --info
)

cp -a source/user-service/* dist/
