:: This batch file compiles the Java files in the src/main directory and creates the client jar and system native executables
:: It does not update the jar file.
:: Rather, it creates new jar files each time it is ran.
@echo off

:: Remove the current content of the out directory.
echo Removing current out folder...
rmdir /S /Q out
echo Folder removed.

:: Create the tmp directory.
echo Creating tmp folder...
mkdir tmp
echo Folder created.

:: Create the out directory while compiling the Java files.
echo Compiling Java files...
javac -cp ".;lib/*" -d out/ src/main/*.java -Xlint
echo Compilation complete.

:: Extract lib files to the out directory.
echo Extracting lib files...
cd out
for /r ..\lib %%i in (*.jar) do (
    jar xvf %%i
)
echo Files extracted.
cd ..

:: Remove MANIFEST.MF files from the out directory.
echo Removing MANIFEST.MF files...
del /S /Q out\META-INF\MANIFEST.MF
echo Files removed.

:: Create the client executable jar file.
echo Creating app.jar...
jar cvfe tmp/app.jar main.App -C out/ .
echo app.jar created.

:: Recreate the out directory.
echo Removing out folder...
rmdir /S /Q out
echo Folder removed.
echo Creating out folder...
mkdir out
echo Folder created.

:: Move the jar files to the out directory.
echo Moving jar files...
move tmp\* out
echo Jar files moved.

:: Remove the tmp directory.
echo Removing tmp folder...
rmdir /Q tmp
echo Folder removed.

:: Create the system native executable.
echo Creating system native executable...
native-image --no-server -jar out/app.jar
echo Executable created.

:: Move the native executable to the out directory.
echo Moving native executable...
move app out
echo Executable moved.

echo Done.