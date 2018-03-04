OAuthClientRequest request = OAuthClientRequest
  .tokenProvider(OAuthProviderType.FACEBOOK)
  .setGrantType(GrantType.AUTHORIZATION_CODE)
  .setClientId("950513172001321")
  .setClientSecret("3b2e464637e5159024254dd78aadb17a")
  .setRedirectURI("http://localhost:8080/facebooklogin")
  .setCode(code)
  .buildQueryMessage();