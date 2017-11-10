#!/bin/bash

# update_R53 -- update dns pointers in Route 53

action=$1

case "$action" in
    UPSERT|CREATE|DELETE )  : ;;
    * )                     echo 'usage: update_R53 UPSERT|CREATE|DELETE';
                            exit 1;;
esac

DOMAIN=escloud.co.uk
ZONEID=Z57L0GQOFE23V
BS_ZONEID=Z2NYPWQ7DFZAZH
PREFIX=eu-west-1.elasticbeanstalk.com

function get_tag {
  query="Tags[?ResourceId==\`$instance_id\`]|[?Key==\`$1\`].Value"
  aws ec2 describe-tags --query "$query" --output=text
}

instance_id=$(curl -s http://169.254.169.254/latest/meta-data/instance-id)
environment=$(get_tag esure:environment                 )
suffix=$(     get_tag esure:suffix                      )
env_name=$(   get_tag elasticbeanstalk:environment-name )

kong_url=${suffix}-${environment}.$DOMAIN
beanstalk_alias=${env_name}.$PREFIX


ETH0=$(/usr/bin/curl -s http://169.254.169.254/latest/meta-data/local-ipv4)
HOSTNAME=$(cat /etc/esure/server-hostname)
echo "Setting up R53 for $HOSTNAME -> $ETH0"

json=/tmp/update_R53.$$.json

cat <<ADDNAME >$json
{
    "Changes": [ {
        "Action": "$action",
        "ResourceRecordSet": {
            "Name": "$HOSTNAME.$DOMAIN.",
            "Type": "A",
            "TTL": 300,
            "ResourceRecords": [ { "Value": "$ETH0" } ]
        }
    },
    {
        "Action": "UPSERT",
        "ResourceRecordSet": {
            "Name": "$kong_url",
            "Type": "A",
            "AliasTarget": {
                "HostedZoneId": "$BS_ZONEID",
                "DNSName": "$beanstalk_alias",
            "EvaluateTargetHealth": false
            }
        }
    } ]
}
ADDNAME

batch=file://$json

aws route53 change-resource-record-sets --change-batch $batch --hosted-zone-id $ZONEID
