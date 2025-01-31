FROM rabbitmq:4.0.4-management-alpine

ENV RABBITMQ_PID_FILE /var/lib/rabbitmq/mnesia/rabbitmq

COPY ./init.sh /opt/app/init.sh
RUN chmod +x /opt/app/init.sh

RUN rabbitmq-plugins enable rabbitmq_stomp --offline

EXPOSE 15672 61613

CMD ["/opt/app/init.sh"]
