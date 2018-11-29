#!/usr/bin/env bash

set -eu

export GRADLE_USER_HOME="$(pwd)/.gradle"

version=$(cat version/version)

(
cd source
./gradlew -Pversion=${version} user-service:clean user-service:build --rerun-tasks --no-daemon
)

cp -a source/* dist/
