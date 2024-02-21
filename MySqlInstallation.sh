#!/bin/bash
 
sudo dnf upgrade -y

echo "Starting with Java Installation."
sudo dnf install java-17-openjdk -y
echo "Completed with Java Installation."
 
# Install MySQL
echo "Installing MySQL"

sudo dnf install mysql-server -y
sudo systemctl start mysqld.service
mysql -u root  -e  "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';"
 
# Start and enable MySQL service
sudo systemctl start mysqld
sudo systemctl enable mysqld

echo "Completed Installing MySQL"

