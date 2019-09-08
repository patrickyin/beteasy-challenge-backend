#!/bin/sh

PROJECT_PATH=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
DIRECTORY_NAME=$( basename ${PROJECT_PATH})
APP_FILE=${PROJECT_PATH}/build/install/${DIRECTORY_NAME}/bin/${DIRECTORY_NAME}
INSTALL_COMMAND="${PROJECT_PATH}/gradlew --project-dir=${PROJECT_PATH} installDist"

${INSTALL_COMMAND} &> /dev/null

${APP_FILE} "$@"