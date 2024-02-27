#!/bin/bash
 
sudo dnf upgrade -y

echo "Starting with Java Installation."
sudo dnf install java-17-openjdk -y
java -version
echo "Completed with Java Installation."
 