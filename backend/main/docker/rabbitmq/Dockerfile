FROM rabbitmq:4.0.4-management-alpine

ENV RABBITMQ_PID_FILE /var/lib/rabbitmq/mnesia/rabbitmq

RUN rabbitmq-plugins enable rabbitmq_stomp --offline

EXPOSE 15672 61613
