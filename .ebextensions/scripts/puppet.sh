#!/bin/bash

function log { echo $(date '+%Y-%m-%d %a %H:%M:%S') ______________ $*; }

function get_tag {
    query="Tags[?ResourceId==\`$instance_id\`]|[?Key==\`$1\`].Value"
    aws ec2 describe-tags --query "$query" --output=text
}

instance_id=$(curl -s http://169.254.169.254/latest/meta-data/instance-id)
environment=$(get_tag esure:environment)

#__________________________________________________
log 'Enable epel repo..'
yum-config-manager --enable epel

#__________________________________________________
log 'reset yum..'
yum clean all

#__________________________________________________
log 'Configure official Puppet repo..'
rpm -Uvh https://yum.puppetlabs.com/puppetlabs-release-pc1-el-6.noarch.rpm

#__________________________________________________
log 'Install/Upgrade Some Packages..'
yum -y install git puppet-agent docker
/opt/puppetlabs/puppet/bin/gem install hiera-eyaml

log 'Chaning key permissions'
chmod 0600 /etc/puppetlabs/puppet/ssl/private_keys/private_key.pkcs7.pem
chmod 0600 /etc/puppetlabs/puppet/ssl/public_keys/public_key.pkcs7.pem
chmod 0600 /root/.ssh/github

#__________________________________________________
log 'Configure local Puppet files for puppets environment..'

echo "environment = $environment" >>/etc/puppetlabs/puppet/puppet.conf
mkdir -p /etc/puppetlabs/code/environments/$environment

#__________________________________________________
log 'Git-Pull latest Puppet config info..'


mkdir -p /etc/puppetlabs/puppet/hieradata
cd       /etc/puppetlabs/puppet/hieradata
git clone -b hiera git@github.com:esure-dev/puppet-hiera.git hiera

mkdir -p /etc/puppetlabs/code/modules/
cd       /etc/puppetlabs/code/modules/
git clone -b future_production git@github.com:esure-dev/puppet-accounts.git accounts
git clone -b future_production git@github.com:esure-dev/puppet-common.git common
git clone -b future_production git@github.com:esure-dev/puppet-sudoers.git sudoers

#__________________________________________________
log 'Run Puppet..'

/opt/puppetlabs/bin/puppet apply --verbose -e "include accounts" || exit 1
/opt/puppetlabs/bin/puppet apply --verbose -e "include sudoers"  || exit 1
