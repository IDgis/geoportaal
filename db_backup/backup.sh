#!/bin/sh

if [ -f /var/run/backup.pid ]; then
	if [ -d /proc/$(cat /var/run/backup.pid) ]; then	
		echo backup is already running... $(date)
		exit 0
	else
		echo obsolete pid file found: /var/run/backup.pid
	fi
fi

echo $$ > /var/run/backup.pid

echo Performing remote backup: $(date)

# loading settings
. /etc/backup

mkdir $BACKUP_SOURCE
cd $BACKUP_SOURCE

# dump database
pg_dump -v -h $DB_HOST -p $DB_PORT -F c -f $DB_NAME -U $DB_USER $DB_NAME

# perform an incremental backup using duplicity:
echo "Performing incremental backup ..."
duplicity incremental --allow-source-mismatch --no-encryption --no-compression --full-if-older-than=7D $BACKUP_SOURCE ftp://$BACKUP_USER:$BACKUP_PASSWORD@$BACKUP_HOST/$BACKUP_NAME


echo "Removing old backups..."
duplicity remove-older-than --allow-source-mismatch  14D ftp://$BACKUP_USER:$BACKUP_PASSWORD@$BACKUP_HOST/$BACKUP_NAME

echo Backup files:
du -h $BACKUP_SOURCE/*
echo backup finished: $(date)

rm /var/run/backup.pid