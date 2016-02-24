sudo systemctl start mysqld.service
echo Please enter password for MySQL root user.
mysql -u root -p

if [[ -z $1 ]]; then
	sudo systemctl stop mysqld.service
fi
