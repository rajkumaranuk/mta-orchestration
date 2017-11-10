#!/bin/bash
# A script to generate a typical esure hostname

function log { echo $(date '+%Y-%m-%d %a %H:%M:%S') ______________ $*; }

function get_tag {
  query="Tags[?ResourceId==\`$instance_id\`]|[?Key==\`$1\`].Value"
  aws ec2 describe-tags --query "$query" --output=text
}

#__________________________________________________
log 'Start ESURE Server Configuration for Beanstalk..'

region=aws
base_seq=01
instance_id=$(curl -s http://169.254.169.254/latest/meta-data/instance-id)
avz=$(        curl -s http://169.254.169.254/latest/meta-data/placement/availability-zone | cut -d- -f3)
ip=$(         curl -s http://169.254.169.254/latest/meta-data/local-ipv4)

environment=$(get_tag esure:environment)
suffix=$(     get_tag esure:suffix     )
role=$(       get_tag esure:role       )

if [ -z "$role" -o -z "$environment" ]; then
  log 'FATAL: setup.sh: esure:role or esure:environment ec2 tag not present.  Cannot proceed with Esure Ops setup steps'
  exit 1
fi

aws s3 cp --recursive s3://esure-puppet-eyaml/files/ /
chmod 0600 /root/.aws/config
chmod 0600 /root/.ssh/id_rsa


#__________________________________________________
log 'Overwrite default system config files with environment-sensitive files..'
# note 1 the recursive switch ensures this will not complain even if the source folder does not exist!
# note 2 need to map prep=>prep, prod=>prod, tst.=>tst, dev.=>dev.   (i.e. "scope" concept)

case $environment in
  prep|prod) scope=$environment;;
  *)         scope=${environment:0:3};;
esac

# This line may be required if using s3 to store confidential environment specific secrets.
# aws s3 cp --recursive s3://esure-puppet-eyaml/$scope/ /

#__________________________________________________
log 'Generate new hostname..'

# Use a pessimistic exclusive-lock algorithm by writing a lock file into an S3 admin bucket
# to ensure multiple scaling instances are not generating the same seqno.

log "Instance seqno generation for instance_id $instance_id availability-zone $avz"
if [[ "$suffix" ]]; then
log "Given region $region role $role environment $environment base-seq $base_seq"
else
log "Given region $region role $role environment $environment base-seq $base_seq suffix $suffix"
fi

bucket=esure-beanstalk-admin
app=$region-es$role-$environment

ctries=1 # contention-tries
while true; do
ltries=1 # lock-tries
while true; do
aws s3 ls s3://$bucket/$app-lock >/dev/null || break
log "awaiting release of lockfile at s3://$bucket/$app-lock; $ltries of 10 ltries so far"
if ((ltries++>=10)); then
  log "fatal: giving up on s3://$bucket/$app-lock"
  exit 1
fi
sleep 1
done
echo $instance_id >$app-lock
log write and immediately re-read s3://$bucket/$app-lock..
aws s3 cp --quiet $app-lock s3://$bucket/
aws s3 cp --quiet s3://$bucket/$app-lock .
locker=$(<$app-lock)
[[ $instance_id == $locker ]] && break
log "Unlikely edge case: My attempt to lock beaten by peer $locker."
if ((ctries++>=10)); then
log "fatal: an instance this unlucky should not be allowed to live"
exit 1
fi
done
log "lock verified. get $app entry if exists.."
aws s3 cp --quiet s3://$bucket/$app . || touch $app
if grep $instance_id $app >/dev/null; then
seq=$(awk "/$instance_id/ {print \$1}" $app)
log "I am old instance $seq in $app file:"
else
instances=$(wc -l <$app)
bigseq=$((base_seq + instances))
seq=$((bigseq % 100))
if [[ $seq == 0 ]]; then
echo "$bigseq (none)" >>$app
: $((bigseq++))
seq=1
fi
echo "$bigseq $instance_id" >>$app
aws s3 cp --quiet $app s3://$bucket/
log "I will be new instance number $seq in $app file:"
fi
aws s3 rm --quiet s3://$bucket/$app-lock
cat $app
rm $app $app-lock


if [[ "$suffix" ]]; then
ESURE_HOSTNAME=$(printf 'e%s-es%s-%s-%02d-%s\n' $avz $role $environment $seq $suffix)
else
ESURE_HOSTNAME=$(printf 'e%s-es%s-%s-%02d\n' $avz $role $environment $seq)
fi
log "Generated hostname is $ESURE_HOSTNAME"

#__________________________________________________
log 'Set Hostname..'
# reference: https://aws.amazon.com/premiumsupport/knowledge-center/linux-static-hostname-rhel7-centos7/   and ..
# http://www.nightbluefruit.com/blog/2013/04/how-to-change-the-hostname-on-an-amazon-linux-system-without-rebooting/
echo "$ESURE_HOSTNAME"           >/etc/hostname
echo "$ESURE_HOSTNAME"           >/proc/sys/kernel/hostname
echo "HOSTNAME=$ESURE_HOSTNAME" >>/etc/sysconfig/network
echo 'preserve_hostname: true'   >>/etc/cloud/cloud.cfg
hostname $ESURE_HOSTNAME
export HOSTNAME=$ESURE_HOSTNAME

#__________________________________________________
log 'Set my EC2 Name Tag now and again in 60 minutes (in case Beanstalk overwrites it!)..'
echo "aws ec2 create-tags --resources $instance_id --tags Key=Name,Value=$ESURE_HOSTNAME" | at 'now'
echo "aws ec2 create-tags --resources $instance_id --tags Key=Name,Value=$ESURE_HOSTNAME" | at 'now + 60 minutes'

#__________________________________________________
log 'Restart syslog to pick up hostname..'
service rsyslog restart

#__________________________________________________
log 'Write context info to shared /etc/esure area'
mkdir -p /etc/esure
cd /etc/esure
echo $ip       >server-ip
echo $HOSTNAME >server-hostname
date           >server-build-date
env            >server-env


