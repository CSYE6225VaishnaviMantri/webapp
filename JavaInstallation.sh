#!/bin/bash
 
sudo dnf update -y
sudo dnf upgrade -y
 
echo "Starting with Java Installation."

sudo yum -y install wget vim
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
sudo yum -y install ./jdk-17_linux-x64_bin.rpm

# Update jdk version
echo "Updating JDK Version"
export JAVA_HOME=/usr/lib/jvm/jdk-17-oracle-x64/>> ~/.source /etc/environment
 
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