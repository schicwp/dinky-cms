
[Back](../)

## API Authentication

### Logging In

To authenticate as a user, you submit credentials to a token endpoint, and then use the token for subsequent requests. 

    POST /api/v1/auth/token
    
Post body:

```json
{
  "username":"joe",
  "password":"bob"
}
```

The service will respond with a token:

```json
{
  "token":"1234567890"
}
```

The token should then be used as a [Bearer Token](https://tools.ietf.org/html/rfc6750) in the http request headers:

    Authorization: Bearer 1234567890
   
### Getting Current User Info

Information about the current user can be requested via the endpoint:

    GET /api/v1/auth/current
    
This requires a valid token. 

## Authentication Providers

One or many authentication providers can be used via various [Spring Sercurity](https://spring.io/projects/spring-security) modules. At a minimum, these provides need to supply a unique username and user roles/groups. 

An example/development security config is [here](src/main/java/org/schicwp/dinky/security/ProviderConfig.java). This is excluded from the binary builds but can serve as a template for other security configurations. 



[Back](../)
