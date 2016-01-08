# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|
	# Use Ubuntu 14.10 as a base:
	config.vm.box = "ubuntu/vivid64"

	# Provision using a shell script:
	config.vm.provision :shell, path: "scripts/provisioning.sh"
	
	# Forward the Postgres port:
	config.vm.network "forwarded_port", guest: 5432, host: 5433
	
	# Forward the Docker daemon port:
	# config.vm.network "forwarded_port", guest: 2375, host: 2375
	
	# Forward the zookeeper port:
	config.vm.network "forwarded_port", guest: 2181, host: 2181
	
	# Forward the exhibitor port:
	config.vm.network "forwarded_port", guest: 8081, host: 8082, auto_correct: true
	
	# Forward the play-app port:
	# config.vm.network "forwarded_port", guest: 9000, host: 9000
	
	# Forward the apache port:
	config.vm.network "forwarded_port", guest: 80, host: 80
	
	# Set shared directory:
	config.vm.synced_folder "/Users/Sandro/git", "/vagrant"
	
	# Configure VirtualBox:
  	config.vm.provider "virtualbox" do |vb|
		# Customize the amount of memory on the VM:
		vb.memory = "2048"
	end
end