FROM node:22.13.1-alpine as builder

ARG SERVER_URL
ARG SOCKJS_URL
ARG JANUS_WS_URL
ARG APP_URL
ARG KEYCLOAK_URL

WORKDIR /opt/app

RUN apk add --no-cache bash
RUN npm install -g @angular/cli@19.0.0

COPY package.json package-lock.json  ./

RUN npm ci --ignore-scripts true

COPY . .

RUN bash -c 'sed -i "s~\${SERVER_URL}~${SERVER_URL}~g" /opt/app/src/environments/environment.springchat-docker.ts'
RUN bash -c 'sed -i "s~\${SOCKJS_URL}~${SOCKJS_URL}~g" /opt/app/src/environments/environment.springchat-docker.ts'
RUN bash -c 'sed -i "s~\${JANUS_WS_URL}~${JANUS_WS_URL}~g" /opt/app/src/environments/environment.springchat-docker.ts'
RUN bash -c 'sed -i "s~\${APP_URL}~${APP_URL}~g" /opt/app/src/environments/environment.springchat-docker.ts'
RUN bash -c 'sed -i "s~\${KEYCLOAK_URL}~${KEYCLOAK_URL}~g" /opt/app/src/environments/environment.springchat-docker.ts'

RUN ng build --configuration="springchat-docker"

FROM nginx:1.27.3-alpine

COPY ./nginx/default.conf /etc/nginx/conf.d/.

COPY --from=builder /opt/app/dist/springchat-frontendmain/browser/. /usr/share/nginx/html/.

