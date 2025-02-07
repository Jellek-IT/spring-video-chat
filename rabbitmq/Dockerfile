FROM rabbitmq:4.0.4-management-alpine

ENV RABBITMQ_PID_FILE /var/lib/rabbitmq/mnesia/rabbitmq

RUN rabbitmq-plugins enable rabbitmq_stomp --offline

ARG STOMP_PASSWORD

COPY ./rabbitmq.conf /etc/rabbitmq/rabbitmq.conf

RUN bash -c 'sed -i "s~\${STOMP_PASSWORD}~${STOMP_PASSWORD}~g" /etc/rabbitmq/rabbitmq.conf'

EXPOSE 15672 61613
