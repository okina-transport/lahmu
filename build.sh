#!/usr/bin/env bash

echo Building docker image


cd build/libs;


# Back
VERSION_JAR=$(ls *lahmu* | sed 's/lahmu-//' | sed 's/.jar//')
echo "VERSION_JAR:"VERSION_JAR

BACK_IMAGE_NAME=registry.okina.fr/mobiiti/lahmu:"${VERSION_JAR}"

# gradle job is done by Jenkins
cd ../..

docker build -t "${BACK_IMAGE_NAME}" --build-arg JAR_FILE=lahmu-${VERSION_JAR}.jar .
docker push "${BACK_IMAGE_NAME}"
