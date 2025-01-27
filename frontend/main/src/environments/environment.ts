export const environment = {
  production: false,
  serverUrl: 'http://localhost:8080/api',
  sockJsUrl: 'http://localhost:8080/api/ws',
  janusWsUrl: 'ws://localhost:8188',
  url: 'http://localhost:4200',
  keycloak: {
    url: 'http://localhost:8090',
    realm: 'local',
    clientId: 'springchat-frontendmain',
    'use-resource-role-mappings': true,
  },
};
