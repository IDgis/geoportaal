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
	zookeeper \
	zookeeper-bin \
	zookeeperd \
	maven \
	docker-engine
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