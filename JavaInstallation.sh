#!/bin/bash
 
sudo dnf update -y
sudo dnf upgrade -y
 
echo "Starting with Java Installation."
sudo yum install java-17-openjdk-devel
 
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk" >> ~/.bashrc
echo "export PATH=$JAVA_HOME/bin:$PATH" >> ~/.bashrc
 
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