#!/bin/bash

echo generating config...

for var in DB_HOST DB_PORT DB_USER DB_PASSWORD DB_NAME BACKUP_USER BACKUP_PASSWORD BACKUP_HOST BACKUP_SOURCE BACKUP_NAME
do
	if [ -z "${!var}" ]; then
		echo $var environment variable is missing
		exit 1
	fi

	echo $var=${!var} >> /etc/backup 
done

# store database connection parameters for pg_dump
echo $DB_HOST:$DB_PORT:$DB_NAME:$DB_USER:$DB_PASSWORD > ~/.pgpass
chmod 0600 ~/.pgpass

mkfifo /opt/fifo
# tigger 'tail -f' to open fifo
echo logging started... > /opt/fifo &

echo "00 5 * * * root /opt/backup.sh > /opt/fifo 2>&1" > /etc/crontab

echo starting cron...

cron
tail -f /opt/fifo