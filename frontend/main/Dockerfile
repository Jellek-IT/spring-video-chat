FROM node:22.13.1-alpine as builder

ARG CONFIGURATION

WORKDIR /opt/app

RUN npm install -g @angular/cli@19.0.0

COPY package.json package-lock.json  ./

RUN npm ci --ignore-scripts true

COPY . .

RUN ng build --configuration="${CONFIGURATION}"

FROM nginx:1.27.3-alpine

COPY ./nginx/default.conf /etc/nginx/conf.d/.

COPY --from=builder /opt/app/dist/frontend-administration/browser/. /usr/share/nginx/html/.

