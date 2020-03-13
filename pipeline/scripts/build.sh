#!/usr/bin/env bash

set -eu

export GRADLE_USER_HOME=".gradle"

version=$(cat version/tag)

(
cd source
./gradlew -Pversion="$version" clean build --rerun-tasks --no-daemon
)

cp -a source/* dist/
