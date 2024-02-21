#!/bin/bash
 
sudo dnf update -y
sudo dnf upgrade -y
 
echo "Starting with MySQL Installation."
sudo dnf install mysql-server -y
 
 
sudo systemctl start mysqld.service
sudo systemctl enable mysqld.service

mysql -u root  -e "CREATE USER 'clouduser'@'localhost' IDENTIFIED BY 'clouduser';"
mysql -u root  -e "GRANT ALL ON *.* TO 'clouduser'@'localhost';"
mysql -u root  -e "FLUSH PRIVILEGES;"
 
# Start and enable MySQL service
sudo systemctl start mysqld
sudo systemctl enable mysqld
 
echo "Completed with MySQL Installation."
 