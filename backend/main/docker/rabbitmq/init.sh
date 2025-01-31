#!/bin/sh

echo 'NODENAME=rabbit@localhost' > /etc/rabbitmq/rabbitmq-env.conf

(rabbitmqctl wait --timeout 60 $RABBITMQ_PID_FILE ; \
rabbitmqctl set_policy queues-expires ".*" '{"expires":10000}' --apply-to queues) &

rabbitmq-server $@
