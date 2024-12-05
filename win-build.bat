@echo off
setlocal enabledelayedexpansion

:: This script compiles Java files in the src\main directory, creates the app jar,
:: and optionally creates a system-native executable based on user choice.

:: Parse command-line options
set CREATE_EXECUTABLE_PROMPT=true
set INVALID_ARGUMENT=false

for %%i in (%*) do (
    if /i "%%i"=="-y" (
        set CREATE_EXECUTABLE_PROMPT=false
        set CREATE_EXECUTABLE=true
    ) else if /i "%%i"=="-n" (
        set CREATE_EXECUTABLE_PROMPT=false
        set CREATE_EXECUTABLE=false
    ) else (
        set INVALID_ARGUMENT=true
    )
)

:: Display usage if invalid argument is detected
if "%INVALID_ARGUMENT%"=="true" goto :usage

:: Remove the current content of the out directory
echo Removing content of the out directory...
if exist out rmdir /S /Q out
echo Content removed.

:: Create the out directory and compile Java files
echo Compiling Java files...
mkdir out
:: Find all Java source files in src\main and list them in sources.txt
dir /s /B src\main\*.java > sources.txt
:: Compile the listed Java files to the out directory
javac -cp ".;lib/*" -d out/ @sources.txt -Xlint
if errorlevel 1 (
    echo Compilation failed. Exiting...
    del sources.txt
    exit /b 1
)
:: Remove the temporary sources.txt file after compilation
del sources.txt
echo Compilation complete.

:: Extract library (JAR) files to the out directory
echo Extracting lib files...
cd out
for /r ..\lib %%i in (*.jar) do (
    jar xvf "%%i"
)
cd ..
echo Files extracted.

:: Remove existing MANIFEST.MF files from the out directory
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

:: Prompt for creating the native executable if no argument was provided
if "%CREATE_EXECUTABLE_PROMPT%"=="true" (
    :: Prompt the user for input: "Do you want to create the native executable?"
    choice /M "Do you want to create the native executable?"
    if errorlevel 2 (
        set CREATE_EXECUTABLE=false
    ) else if errorlevel 1 (
        set CREATE_EXECUTABLE=true
    ) else (
        set CREATE_EXECUTABLE=false
    )
)

:: Create the system-native executable if chosen
if "%CREATE_EXECUTABLE%"=="true" (
    echo Creating system native executable...
    :: Use GraalVM native-image to create a native executable
    call native-image --no-server -jar out\app.jar
    if errorlevel 1 (
        echo Error creating native executable. Exiting...
        exit /b 1
    )
    if exist app.exe (
        :: Move the executable to the out directory
        move app.exe out
        echo Executable moved.
    ) else (
        echo Executable not found. Exiting...
        exit /b 1
    )
) else (
    echo Skipping native executable creation.
)

echo Done.
exit /b 0

:: Function to display usage information
:usage
echo Usage: win-build [-y] [-n]
echo -y: Create the native executable
echo -n: Do not create the native executable
exit /b 1

endlocal