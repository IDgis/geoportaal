FROM ubuntu:vivid

RUN apt-get update \
	&& apt-get install -y --no-install-recommends \
		cron \
		duplicity \
		lftp \
		postgresql-client \
	&& rm -rf /var/lib/apt/lists/*

COPY backup.sh /opt/
COPY start.sh /opt/

RUN chmod u+x /opt/*.sh

ENV BACKUP_HOST="localhost" \
	BACKUP_USER="test" \
	BACKUP_PASSWORD="test" \
	BACKUP_SOURCE="/var/geoportaal-beheer" \
	BACKUP_NAME="geoportaal-beheer-backup" \
	DB_HOST="db" \
	DB_PORT="5432" \
	DB_USER="user" \
	DB_PASSWORD="password" \
	DB_NAME="geoportaal-beheer"

CMD ["/opt/start.sh"]