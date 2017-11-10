#!/bin/bash

function log { echo $(date '+%Y-%m-%d %a %H:%M:%S') ______________ $*; }

function get_tag {
    query="Tags[?ResourceId==\`$instance_id\`]|[?Key==\`$1\`].Value"
    aws ec2 describe-tags --query "$query" --output=text
}

environment=$(get_tag esure:environment)
suffix=$(     get_tag esure:suffix     )
role=$(       get_tag esure:role       )

#__________________________________________________
log 'Install Qualys Cloud Agent..'
cd /root/qualys
rpm -ivh qualys-cloud-agent.x86_64.rpm
qualys=/usr/local/qualys/cloud-agent/bin/qualys-cloud-agent.sh
if [ -z "$suffix" ]; then
if [ -f conf/ActivationId-${role} -a -f conf/CustomerId-${role} ]; then
$qualys ActivationId=$(<conf/ActivationId-${role}) CustomerId=$(<conf/CustomerId-${role})
else
$qualys ActivationId=$(<ActivationId) CustomerId=$(<CustomerId)
fi
else
if [ -f conf/ActivationId-${suffix} -a -f conf/CustomerId-${suffix} ]; then
$qualys ActivationId=$(<conf/ActivationId-${suffix}) CustomerId=$(<conf/CustomerId-${suffix})
else
$qualys ActivationId=$(<ActivationId) CustomerId=$(<CustomerId)
fi
fi

