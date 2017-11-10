#!/bin/bash
repo_path='/etc/puppetlabs/code/modules'
repos=`/bin/ls ${repo_path}`

for repo in $repos; do
  cd ${repo_path}/${repo}
  git pull
done

cd /etc/puppetlabs/puppet/hieradata/hiera
git pull

/opt/puppetlabs/bin/puppet apply -e "include sudoers"
/opt/puppetlabs/bin/puppet apply -e "include accounts::bastion"

