#!/bin/bash
 
sudo dnf update -y
sudo dnf upgrade -y

echo "Starting with Java Installation."
sudo dnf install java-17-openjdk -y
java -version
echo "Completed with Java Installation."

echo "Starting with Maven Installation."
sudo dnf install maven -y
echo "Completed Maven Installation."

