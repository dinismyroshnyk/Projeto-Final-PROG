@echo off
setlocal enabledelayedexpansion

:: Function to display usage information
:usage
echo Usage: %0 [-y ^| -n]
echo   -y: Automatically create the native executable without prompt.
echo   -n: Skip creating the native executable without prompt.
exit /b 1

:: Parse command-line options
set CREATE_EXECUTABLE_PROMPT=true
set CREATE_EXECUTABLE=

:parse_args
if "%~1"=="-y" (
    set CREATE_EXECUTABLE_PROMPT=false
    set CREATE_EXECUTABLE=true
    shift
) else if "%~1"=="-n" (
    set CREATE_EXECUTABLE_PROMPT=false
    set CREATE_EXECUTABLE=false
    shift
) else if "%~1"=="" (
    goto :main
) else (
    call :usage
)
goto :parse_args

:main
:: Remove the current content of the out directory
echo Removing content of the out directory...
if exist out rmdir /S /Q out
echo Content removed.

:: Create the out directory and compile Java files
echo Compiling Java files...
mkdir out
dir /s /B src\main\*.java > sources.txt
javac -cp ".;lib/*" -d out/ @sources.txt -Xlint
if errorlevel 1 (
    echo Compilation failed. Exiting...
    del sources.txt
    exit /b 1
)
del sources.txt
echo Compilation complete.

:: Extract lib files to the out directory
echo Extracting lib files...
cd out
for /r ..\lib %%i in (*.jar) do (
    jar xvf "%%i"
)
cd ..
echo Files extracted.

:: Remove MANIFEST.MF files from the out directory
echo Removing MANIFEST.MF files...
if exist out\META-INF\MANIFEST.MF del /S /Q out\META-INF\MANIFEST.MF
echo Files removed.

:: Create the app executable jar file
echo Creating app.jar...
mkdir tmp
jar cvfe tmp\app.jar main.App -C out/ .
if errorlevel 1 (
    echo Error creating app.jar. Exiting...
    exit /b 1
)
echo app.jar created.

:: Clean and prepare the out directory
echo Removing old out folder...
rmdir /S /Q out
mkdir out
echo Folder ready.

:: Move the jar file to the out directory
echo Moving jar file...
move tmp\* out
rmdir /Q tmp
echo Jar file moved.

:: Prompt for creating the native executable if no command-line option was given
if %CREATE_EXECUTABLE_PROMPT%==true (
    set /p response="Do you want to create the native executable? (y/n): "
    if /I "!response!"=="y" set CREATE_EXECUTABLE=true
    if /I "!response!"=="n" set CREATE_EXECUTABLE=false
)

:: Create the system native executable if chosen
if "%CREATE_EXECUTABLE%"=="true" (
    echo Creating system native executable...
    native-image --no-server -jar out\app.jar
    if errorlevel 1 (
        echo Error creating native executable. Skipping...
    ) else (
        echo Executable created.
        move app out
        echo Executable moved.
    )
) else (
    echo Skipping native executable creation.
)

echo Done.
endlocal