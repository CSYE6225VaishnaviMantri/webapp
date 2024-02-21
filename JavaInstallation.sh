#!/bin/bash
 
sudo dnf update -y
sudo dnf upgrade -y

#!/bin/bash

# Install Java 17
echo "Installing Java"
sudo yum -y install wget vim
wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.rpm
sudo yum -y install ./jdk-17_linux-x64_bin.rpm

# Update jdk version
echo "Updating JDK Version"
export JAVA_HOME=/usr/lib/jvm/jdk-17-oracle-x64/>> ~/.source /etc/environment


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