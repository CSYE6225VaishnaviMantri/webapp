#!/bin/bash
 
sudo dnf update -y
sudo dnf upgrade -y

#!/bin/bash

# Install required tools
sudo yum -y install wget vim

# Download and install Oracle JDK 17
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
sudo yum -y localinstall ./jdk-17_linux-x64_bin.rpm

# Update JDK version and set environment variable
echo "Updating JDK Version"
export JAVA_HOME=/usr/lib/jvm/jdk-17-oracle-x64
echo "export JAVA_HOME=$JAVA_HOME" >> ~/.bashrc
echo "export PATH=\$PATH:\$JAVA_HOME/bin" >> ~/.bashrc

# Make changes to the current session
source ~/.bashrc

# Verify installation
java --version

# Clean up downloaded RPM file
echo "JDK 17 Installation Completed."

echo "Starting with Maven Installation."
sudo dnf install maven -y
echo "Completed Maven Installation."

echo "Starting with Tomcat Installation."
sudo dnf install -y tomcat
 
sudo systemctl start tomcat
sudo systemctl enable tomcat
echo "Completed Installing Tomcat."