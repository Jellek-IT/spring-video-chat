FROM quay.io/keycloak/keycloak:26.0.6 as builder

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

WORKDIR /opt/keycloak

COPY ./themes ./themes

RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:26.0.6
COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
