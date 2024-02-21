#!/bin/bash
 
sudo dnf update -y
sudo dnf upgrade -y

#!/bin/bash

# Install Java 17
echo "Installing Java"
sudo dnf install java-17-openjdk -y

# Clean up downloaded RPM file
java -version
echo "JDK 17 Installation Completed."

# echo "Starting with Maven Installation."
# sudo dnf install maven -y
# echo "Completed Maven Installation."

# echo "Starting with Tomcat Installation."
# sudo dnf install -y tomcat
 
# sudo systemctl start tomcat
# sudo systemctl enable tomcat
# echo "Completed Installing Tomcat."