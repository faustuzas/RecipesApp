@echo off

SET SOURCES_FILE=sources.txt
SET BUILD_DIR=build
SET DIST_DIR=dist
SET LIBS_DIR=libs
SET JAR_NAME=stiklainis.jar

dir /s /B *.java > %SOURCES_FILE%
IF EXIST %BUILD_DIR% rmdir /s /q %BUILD_DIR%
mkdir %BUILD_DIR%
javac @%SOURCES_FILE% -d %BUILD_DIR%
if NOT "%ERRORLEVEL%" == "0" (
    del %SOURCES_FILE%
    exit /b %ERRORLEVEL%
)

del %SOURCES_FILE%

cd %BUILD_DIR%
jar cfm %JAR_NAME% ../META-INF/MANIFEST.MF ../resources ../META-INF .
if NOT "%ERRORLEVEL%" == "0" (
    exit /b %ERRORLEVEL%
)
cd ..

IF EXIST %DIST_DIR% rmdir /s /q %DIST_DIR%
mkdir %DIST_DIR%
copy %BUILD_DIR%\%JAR_NAME% %DIST_DIR%\ > nul
xcopy %LIBS_DIR% %DIST_DIR% > nul