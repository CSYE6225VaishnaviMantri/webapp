#!/bin/bash
sudo dnf update -y
sudo dnf upgrade -y
 
echo "Starting with Java Installation."
sudo dnf install java-17-openjdk -y
echo "Completed with Java Installation."

echo "Starting with MySQL Installation."
sudo dnf install mysql-server -y
sudo systemctl start mysqld.service
mysql -u root  -e "CREATE USER 'clouduser'@'localhost' IDENTIFIED BY 'data123';"
mysql -u root  -e "GRANT ALL ON *.* TO 'clouduser'@'localhost';"
mysql -u root  -e "FLUSH PRIVILEGES;"
sudo systemctl start mysqld
sudo systemctl enable mysqld
echo "Completed with MySQL Installation."
 