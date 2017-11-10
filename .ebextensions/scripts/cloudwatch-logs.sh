#
# A script that will expose logs on the running instance to cloudwatch via awslogs.
# The correct format is as follows
#
# [PATH TO FILE]
# log_group_name=/${environment_name}/PATH_TO_LOG
# log_stram_name=$HOSTNAME
# file=PATH_TO_FILE
#

#!/bin/bash
function get_tag {
  query="Tags[?ResourceId==\`$instance_id\`]|[?Key==\`$1\`].Value"
  aws ec2 describe-tags --query "$query" --output=text
}

instance_id=$(curl -s http://169.254.169.254/latest/meta-data/instance-id)
environment_name=$(get_tag elasticbeanstalk:environment-name)

# beanstalk logs
cat <<-EOF > /etc/awslogs/config/beanstalklogs.conf
[/var/log/ecs/ecs-init.log]
log_group_name=/${environment_name}/var/log/ecs/ecs-init.log
log_stream_name=$HOSTNAME
file=/var/log/ecs/ecs-init.log*

[/var/log/eb-ecs-mgr.log]
log_group_name=/${environment_name}/var/log/eb-ecs-mgr.log
log_stream_name=$HOSTNAME
file=/var/log/eb-ecs-mgr.log*

[/var/log/ecs/ecs-agent.log]
log_group_name=/${environment_name}/var/log/ecs/ecs-agent.log
log_stream_name=$HOSTNAME
file=/var/log/ecs/ecs-agent.log*

[/var/log/docker-events.log]
log_group_name=/${environment_name}/var/log/docker-events.log
log_stream_name=$HOSTNAME
file=/var/log/docker-events.log*

[/var/log/eb-activity.log]
log_group_name=/${environment_name}/var/log/eb-activity.log
log_stream_name=$HOSTNAME
file=/var/log/eb-activity.log*
EOF

# dmesg log
cat <<-EOF > /etc/awslogs/config/system.conf
[/var/log/dmesg]
log_group_name=/${environment_name}/var/log/dmesg
log_stream_name=$HOSTNAME
file=/var/log/dmesg

[/var/log/messages]
log_group_name=/${environment_name}/var/log/messages
log_stream_name=$HOSTNAME
file=/var/log/messages
EOF

# nginx health log
cat <<-EOF > /etc/awslogs/config/nginx-healthd.conf
[/var/log/nginx/healthd/application]
log_group_name=/${environment_name}/var/log/nginx/healthd/application.log
log_stream_name=$HOSTNAME
file=/var/log/nginx/healthd/application.log.*
EOF

# container log
cat <<-EOF > /etc/awslogs/config/containers.conf
[/var/log/containers/api-jva-home-pricing-orchestration]
log_group_name=/${environment_name}/var/log/containers/api-jva-home-pricing-orchestration
log_stream_name=$HOSTNAME
file=/var/log/containers/api-jva-home-pricing-orchestration-*.log
EOF

