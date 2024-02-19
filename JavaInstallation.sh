#!/bin/bash
 
sudo dnf update -y
sudo dnf upgrade -y
 
echo "Starting with Java Installation."
sudo dnf install java-17-openjdk -y
 
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk.x86_64" >> ~/.bashrc
echo "export PATH=\$PATH:\$JAVA_HOME/bin" >> ~/.bashrc
 
echo "Printing Java Location."
java --version
echo "Completed Installing Java Successfully."
 
echo "Starting with Maven Installation."
sudo dnf install maven -y
echo "Completed Maven Installation."

echo "Starting with Tomcat Installation."
sudo dnf install -y tomcat
 
sudo systemctl start tomcat
sudo systemctl enable tomcat
echo "Completed Installing Tomcat."