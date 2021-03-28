#!/bin/bash
set -e

cd exp-tracker-api
./gradlew installDist
cd ..
docker build -t exp-tracker-api ./exp-tracker-api
