#!/bin/bash
 
sudo dnf update -y
sudo dnf upgrade -y
 
echo "Starting with MySQL Installation."
sudo dnf install mysql-server -y
 
sudo systemctl start mysqld
sudo systemctl enable mysqld
 
SECURE_INSTALLATION=$(expect -c "
spawn sudo mysql_secure_installation
expect \"Enter current password for root (enter for none):\"
send \"\r\"
expect \"Set root password?\"
send \"y\r\"
expect \"New password:\"
send \"data123\r\"
expect \"Re-enter new password:\"
send \"data123\r\"
expect \"Remove anonymous users?\"
send \"y\r\"
expect \"Disallow root login remotely?\"
send \"y\r\"
expect \"Remove test database and access to it?\"
send \"y\r\"
expect \"Reload privilege tables now?\"
send \"y\r\"
expect eof
")
 
echo "$SECURE_INSTALLATION"
 
echo "Completed with MySQL Installation."
 