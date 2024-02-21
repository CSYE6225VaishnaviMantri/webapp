#!/bin/bash
 
sudo dnf upgrade -y
sudo dnf install java-17-openjdk -y
 
# Install MySQL
echo "Installing MySQL"
sudo dnf install mysql-server -y
sudo systemctl start mysqld.service
mysql -u root  -e "CREATE DATABASE UserDatabase;"
mysql -u root  -e "CREATE USER 'clouduser'@'localhost' IDENTIFIED BY 'clouduser';"
mysql -u root  -e "GRANT ALL ON . TO 'clouduser'@'localhost';"
mysql -u root  -e "FLUSH PRIVILEGES;"
 
# Start and enable MySQL service
sudo systemctl start mysqld
sudo systemctl enable mysqld