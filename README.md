# seasonal-calendar

[![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/seasonal-calendar.svg?branch=master)](http://travis-ci.org/AtlasOfLivingAustralia/seasonal-calendar) 

Current version: 1.0-SNAPSHOT

Deploying a new version of seasonal-calendar to Nexus
================================================

Travis-CI is used to deploy new versions of seasonal-calendar to Nexus. This is done automatically by updating the version number in the application.properties file and pushing to GitHub.

Once the new version of seasonal-calendar is deployed to Nexus, the version number in ansible-inventories needs to change. To do this, the version number must be changed in: 

https://github.com/AtlasOfLivingAustralia/ansible-inventories/blob/master/fc-test.ala.org.au

Deploying the current Nexus deployed version of seasonal-calendar to a virtual machine
===========================================================================

If you have not yet installed Ansible, Vagrant, or VirtualBox, use the instructions at the [ALA Install README.md file](https://github.com/AtlasOfLivingAustralia/ala-install/blob/master/README.md) to install those first for your operating system.

Then, to deploy seasonal-calendar onto a local virtual box install use the following instructions:

```
$ cd gitrepos
$ git clone git@github.com:AtlasOfLivingAustralia/ala-install.git
$ (cd ala-install/vagrant/ubuntu-trusty && vagrant up)
```

Add a line to your /etc/hosts file with the following information, replacing '10.1.1.3' with whatever IP address is assigned to the virtual machine that Vagrant starts up in VirtualBox:

```
10.1.1.3 fc-test.ala.org.au
```

Then you can clone the ansible instructions and install it onto the given machine:

```
$ git clone git@github.com:AtlasOfLivingAustralia/ansible-inventories.git
$ ansible-playbook -i ansible-inventories/fc-test.ala.org.au ala-install/ansible/seasonalcalendar-test.yml --private-key ~/.vagrant.d/insecure_private_key -vvvv --user vagrant --sudo
```

Deploying to a production server
======================================

After testing locally, the same ansible scripts can be used to deploy to a production server.

Comment out any testing line for fc-test.ala.org.au in your /etc/hosts file and add the following line, replacing the IP address with the address that you want to deploy the seasonal-calendar to:

```
XX.YY.ZZ.AA fc-test.ala.org.au
```

Then deploy to that machine using the following command, replacing "MY_USER_NAME" with your login username:

```
$ ansible-playbook --user MY_USER_NAME -i ansible-inventories/fc-test.ala.org.au ala-install/ansible/seasonalcalendar-test.yml --private-key ~/.ssh/id_rsa -vvvv --sudo --ask-sudo-pass
```
