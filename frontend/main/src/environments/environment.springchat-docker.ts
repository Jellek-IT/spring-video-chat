export const environment = {
  production: true,
  serverUrl: '${SERVER_URL}',
  sockJsUrl: '${SOCKJS_URL}',
  janusWsUrl: '${JANUS_WS_URL}',
  url: '${APP_URL}',
  keycloak: {
    url: '${KEYCLOAK_URL}',
    realm: 'spring-chat',
    clientId: 'springchat-frontendmain',
    'use-resource-role-mappings': true,
  },
};
