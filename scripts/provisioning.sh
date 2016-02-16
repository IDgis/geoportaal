#!/bin/bash

EXHIBITOR_VERSION=1.5.5

set -e

# Install software and update:
apt-key adv --keyserver hkp://pgp.mit.edu:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
echo "deb https://apt.dockerproject.org/repo ubuntu-vivid main" > /etc/apt/sources.list.d/docker.list
apt-get -qy update
apt-get -qy install \
	byobu \
	wget \
	lftp \
	zookeeper \
	zookeeper-bin \
	zookeeperd \
	maven \
	docker-engine \
	db-util \
	vsftpd
apt-get -qy upgrade

# Configure the docker daemon:
  cp /vagrant/scripts/docker-settings /etc/default/docker
  mkdir -p /etc/systemd/system/docker.service.d
  cp /vagrant/scripts/docker.conf /etc/systemd/system/docker.service.d/

  systemctl daemon-reload
  service docker restart

  cp /vagrant/scripts/docker-env.sh /etc/profile.d/

# Install docker compose:
  curl -L https://github.com/docker/compose/releases/download/1.5.2/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
  chmod a+x /usr/local/bin/docker-compose

# Setup zookeeper and exhibitor: 
if [[ ! -e /opt/exhibitor ]]; then

	# Package exhibitor:
	cd /opt
	mkdir exhibitor
	cd exhibitor
	wget -q https://raw.github.com/Netflix/exhibitor/master/exhibitor-standalone/src/main/resources/buildscripts/standalone/maven/pom.xml
	mvn package
	cd target
	cp exhibitor*.jar /opt/exhibitor.jar 
	
	cat > /etc/init/exhibitor.conf <<-EOT
		description "Exhibitor"
		
		start on vagrant-mounted
		stop on runlevel [!2345]
		
		expect fork
		
		respawn
		respawn limit 0 5
		
		script
			cd /opt
			java -jar exhibitor.jar --port 8081 -c file > /var/log/exhibitor.log 2>&1
			emit exhibitor-running
		end script	
EOT

#	service exhibitor start

fi

# Install PostgreSQL and PostGIS if it hasn't been installed yet:
dpkg -s postgresql-9.4 > /dev/null 2>&1 && {
	echo "PostgreSQL is installed. Skipping ..."
} || {
	echo "PostgreSQL is not installed. Installing ..."
	apt-get update \
		&& apt-get install -y --no-install-recommends \
			postgresql-9.4 \
			postgresql-9.4-postgis-2.1 \
			postgresql-contrib-9.4
			
	echo "host all all 0.0.0.0/0 md5" >> /etc/postgresql/9.4/main/pg_hba.conf
	echo "listen_addresses='*'" >> /etc/postgresql/9.4/main/postgresql.conf
	
	sudo -u postgres psql -c "alter user postgres with password 'postgres';"
	
	service postgresql restart
}

# Create a database if it doesn't exist yet:
if [ $(sudo -u postgres psql --list -qA | grep geoportaal-beheer | wc -l) -eq 0 ]; then
	echo "Creating geoportaal-beheer database ..."
	
	sudo -u postgres createdb -l en_US.UTF-8 -E UTF-8 geoportaal-beheer
fi

# Install a FTP server and configure it:
sudo cp /vagrant/scripts/vsftpd.conf /etc/vsftpd.conf
mkdir /etc/vsftpd
cd /etc/vsftpd
echo user >> vusers.txt
echo password >> vusers.txt
db_load -T -t hash -f vusers.txt vsftpd-virtual-user.db
chmod 600 vsftpd-virtual-user.db
rm vusers.txt
touch /etc/pam.d/vsftpd.virtual
echo auth       required     pam_userdb.so db=/etc/vsftpd/vsftpd-virtual-user >> /etc/pam.d/vsftpd.virtual
echo account    required     pam_userdb.so db=/etc/vsftpd/vsftpd-virtual-user >> /etc/pam.d/vsftpd.virtual
echo session    required     pam_loginuid.so >> /etc/pam.d/vsftpd.virtual
mkdir -p /home/vftp/user
chown -R ftp:ftp /home/vftp
service vsftpd restart