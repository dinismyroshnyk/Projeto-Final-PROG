#!/bin/bash

# This script compiles Java files in the src/main directory, creates the app jar,
# and optionally creates a system-native executable based on user choice.

# Function to display usage information
function usage() {
    echo "Usage: $0 [-y | -n]"
    echo "  -y: Automatically create the native executable without prompt."
    echo "  -n: Skip creating the native executable without prompt."
    exit 1
}

# Parse command-line options
CREATE_EXECUTABLE_PROMPT=true
while getopts "yn" opt; do
    case "$opt" in
        y) CREATE_EXECUTABLE_PROMPT=false; CREATE_EXECUTABLE=true ;;
        n) CREATE_EXECUTABLE_PROMPT=false; CREATE_EXECUTABLE=false ;;
        *) usage ;;
    esac
done

# Remove the current content of the out directory
echo "Removing content of the out directory..."
rm -rf out
echo "Content removed."

# Create the out directory and compile Java files
echo "Compiling Java files..."
mkdir -p out
find src/main -name "*.java" > sources.txt
javac -cp ".:lib/*" -d out @sources.txt -Xlint
if [ $? -ne 0 ]; then
    echo "Compilation failed. Exiting..."
    rm -f sources.txt
    exit 1
fi
rm -f sources.txt
echo "Compilation complete."

# Extract lib files to the out directory
echo "Extracting lib files..."
cd out
for i in ../lib/*.jar; do
    jar xvf "$i"
done
cd ..
echo "Files extracted."

# Remove MANIFEST.MF files from the out directory
echo "Removing MANIFEST.MF files..."
rm -f out/META-INF/MANIFEST.MF
echo "Files removed."

# Create the app executable jar file
echo "Creating app.jar..."
mkdir -p tmp
jar cvfe tmp/app.jar main.App -C out/ .
if [ $? -ne 0 ]; then
    echo "Error creating app.jar. Exiting..."
    exit 1
fi
echo "app.jar created."

# Clean and prepare the out directory
echo "Removing old out folder..."
rm -rf out
mkdir -p out
echo "Folder ready."

# Move the jar file to the out directory
echo "Moving jar file..."
mv tmp/* out
rm -rf tmp
echo "Jar file moved."

# Prompt for creating the native executable if no command-line option was given
if $CREATE_EXECUTABLE_PROMPT; then
    read -p "Do you want to create the native executable? (y/n): " response
    case "$response" in
        [Yy]*) CREATE_EXECUTABLE=true ;;
        [Nn]*) CREATE_EXECUTABLE=false ;;
        *) echo "Invalid input. Skipping executable creation." ;;
    esac
fi

# Create the system native executable if chosen
if [ "$CREATE_EXECUTABLE" = true ]; then
    echo "Creating system native executable..."
    native-image --no-server -jar out/app.jar
    if [ $? -ne 0 ]; then
        echo "Error creating native executable. Skipping..."
    else
        echo "Executable created."
        mv app out
        echo "Executable moved."
    fi
else
    echo "Skipping native executable creation."
fi

echo "Done."