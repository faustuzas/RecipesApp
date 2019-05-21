#!/usr/bin/env bash

SOURCES_FILE=sources.txt
BUILD_DIR=build
DIST_DIR=dist
LIBS_DIR=libs
JAR_NAME=recipe-app.jar

if [[ -d "${BUILD_DIR}" ]]; then
    rm -rf ${BUILD_DIR}
fi
mkdir ${BUILD_DIR}

find -name "*.java" > ${SOURCES_FILE}
javac @${SOURCES_FILE} -d ${BUILD_DIR}

ERROR_CODE=$?
if [[ ${ERROR_CODE} -ne 0 ]] ; then
    rm ${SOURCES_FILE}
    exit ${ERROR_CODE}
fi
rm ${SOURCES_FILE}

cd ${BUILD_DIR}
jar cfm ${JAR_NAME} ../META-INF/MANIFEST.MF ../resources ../META-INF .
ERROR_CODE=$?
if [[ ${ERROR_CODE} -ne 0 ]] ; then
    exit ${ERROR_CODE}
fi
cd ..

if [[ -d "${DIST_DIR}" ]]; then
    rm -rf ${DIST_DIR}
fi
mkdir ${DIST_DIR}

cp ${BUILD_DIR}/${JAR_NAME} ${DIST_DIR}/ > /dev/null
cp ${LIBS_DIR} ${DIST_DIR} > /dev/null