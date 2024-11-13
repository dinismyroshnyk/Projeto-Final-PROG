#!/bin/bash

# This script compiles the Java files in the src/main directory and creates the app jar and system native executables.
# It does not update the jar file.
# Rather, it creates new jar files each time it is ran.

# Remove the current content of the out directory.
echo "Removing content of the out directory..."
rm -rf out
echo "Content removed."

# Create the out directory while compiling the Java files.
echo "Compiling Java files..."
javac -cp ".:lib/*" -d out/ src/main/*.java -Xlint
echo "Compilation complete."

# Extract lib files to the out directory.
echo "Extracting lib files..."
cd out
for i in ../lib/*.jar; do
    jar xvf "$i"
done
echo "Files extracted."
cd ..

# Remove MANIFEST.MF files from the out directory.
echo "Removing MANIFEST.MF files..."
rm -f out/META-INF/MANIFEST.MF
echo "Files removed."

# Create the app executable jar file.
echo "Creating app.jar..."
jar cvfe tmp/app.jar main.App -C out/ .
echo "app.jar created."

# Recreate the out directory.
echo "Removing out folder..."
rm -rf out
echo "Folder removed."
echo "Creating out folder..."
mkdir out
echo "Folder created."

# Move the jar files to the out directory.
echo "Moving jar files..."
mv tmp/* out
echo "Jar files moved."

# Remove the tmp directory.
echo "Removing tmp folder..."
rm -rf tmp
echo "Folder removed."

# Create the system native executable.
echo "Creating system native executable..."
native-image --no-server -jar out/app.jar
echo "Executable created."

# Move the native executable to the out directory.
echo "Moving native executable..."
mv app out
echo "Executable moved."

echo "Done."