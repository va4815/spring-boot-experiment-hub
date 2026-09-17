# AGENTS.md

## Project Overview

This project is an authentication experiment demonstrating:

**Stateful Bearer Authentication with an Opaque Access Token**

The purpose is to understand how opaque bearer tokens work in a Spring Boot application and how stateful token-based authentication differs from stateless JWT authentication.

This project is part of the `spring-boot-experiment-hub`.

---

## Learning Objectives

The implementation should demonstrate:

- Bearer token authentication.
- Opaque access token generation.
- Server-side access token storage.
- Stateful authentication.
- Access token lookup and validation on every authenticated request.
- Access token expiration.
- Access token revocation.
- Login and logout flows.
- Loading authenticated user and role information.
- Spring Security authentication flow.

This experiment uses **access tokens only**.

Do not implement refresh tokens.

---

## Authentication Model

```text
Bearer
  +
Opaque Access Token
  +
Server-side Token Store
  +
Stateful Authentication
```

The opaque token contains no user information or claims.

The server maintains the authentication state associated with each issued token.

---

## Login Flow

1. Client sends username and password to `/auth/login`.
2. Server authenticates the credentials using Spring Security.
3. Server generates a cryptographically secure random opaque access token.
4. Server stores the access token and its associated user information.
5. Server returns the access token to the client.

```text
POST /auth/login

username + password
        |
        v
AuthenticationManager
        |
        v
UserDetailsService
        |
        v
Authentication successful
        |
        v
Generate opaque access token
        |
        v
Store access token server-side
        |
        v
Return access token
```

There is no refresh token.

When the access token expires, the client must authenticate again.

---

## Authenticated Request Flow

The client sends:

```http
Authorization: Bearer <opaque-access-token>
```

For every protected request, the server must:

1. Extract the bearer token.
2. Look up the access token in the server-side token store.
3. Verify that the token exists.
4. Verify that the token has not expired.
5. Verify that the token has not been revoked.
6. Resolve the authenticated user.
7. Populate the Spring Security `SecurityContext`.
8. Continue processing the request.

Conceptually:

```text
Client
  |
  | Authorization: Bearer <opaque-token>
  v
Spring Security
  |
  v
Token Authentication
  |
  v
Server-side Token Store
  |
  +---- token exists?
  |
  +---- token expired?
  |
  +---- token revoked?
  |
  v
Authenticated User
  |
  v
SecurityContext
  |
  v
Controller
```

Unlike JWT authentication, authentication information cannot be determined from the access token itself.

---

## Logout Flow

Logout should invalidate the current opaque access token.

```text
POST /auth/logout
       |
       v
Extract Bearer Token
       |
       v
Find Access Token
       |
       v
Revoke/Delete Token
       |
       v
Token can no longer be used
```

A subsequent request using the same token must return:

```text
401 Unauthorized
```

---

## Technical Stack

Use:

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Maven
- JUnit 5
- Mockito

Use the versions defined by the project's Maven configuration unless specifically required otherwise.

---

## Domain Model

### `role`

```text
id
code
name
```

Example roles:

```text
SHOP_MANAGER
STAFF
```

### `user_account`

```text
id
username
password
role_id
```

Passwords must be encoded using BCrypt or another appropriate Spring Security password encoder.

Never store plaintext passwords.

### `access_token`

The access-token table should contain enough information to manage authentication state.

For example:

```text
id
token
user_id
issued_at
expires_at
revoked
```

The exact schema may evolve during the experiment.

---

## Opaque Access Token Requirements

The access token must:

- Be opaque.
- Be generated using a cryptographically secure random source.
- Have sufficient entropy to prevent practical guessing.
- Contain no meaningful user information.
- Contain no username.
- Contain no user ID.
- Contain no role or authority information.
- Contain no JWT-style claims.
- Have an expiration time.
- Be revocable by the server.

The token should function only as an identifier for server-side authentication state.

Do not implement the opaque access token as a JWT.

---

## Token Storage

Use PostgreSQL as the initial server-side token store so the authentication state can be inspected during the experiment.

Keep token persistence separated from the authentication logic.

For example:

```java
public interface AccessTokenStore {

    Optional<AccessToken> findByToken(String token);

    AccessToken save(AccessToken token);

    void revoke(String token);
}
```

The design may allow another storage mechanism such as Redis to be explored later, but Redis is not required for this experiment.

---

## Security Requirements

Use Spring Security as the primary authentication framework.

Protected APIs must require:

```http
Authorization: Bearer <opaque-access-token>
```

Return:

```text
401 Unauthorized
```

when the access token is:

- Missing
- Invalid
- Expired
- Revoked

Do not send access tokens using:

- URL query parameters
- URL paths
- Cookies
- Request bodies for normal authenticated requests

Do not log:

- Passwords
- Raw access tokens
- Authorization headers

---

## Spring Security

Authentication should integrate with Spring Security concepts such as:

```text
SecurityFilterChain
AuthenticationManager
AuthenticationProvider
UserDetailsService
SecurityContext
```

Keep credential authentication and bearer-token authentication as separate responsibilities.

```text
LOGIN

Username + Password
        |
        v
Credential Authentication
        |
        v
Generate Opaque Access Token


PROTECTED REQUEST

Opaque Access Token
        |
        v
Token Authentication
        |
        v
Server-side Token Lookup
        |
        v
SecurityContext
```

Do not create a separate authentication framework outside Spring Security when Spring Security provides an appropriate extension point.

---

## Suggested API Scope

Keep the API surface small.

```text
POST /auth/login
POST /auth/logout

GET /users/me
GET /roles

GET /public/**
```

`/auth/login` and `/public/**` should be accessible without authentication.

Protected endpoints require a valid opaque access token.

There is deliberately no endpoint such as:

```text
POST /auth/refresh
```

---

## Testing Requirements

At minimum, test:

- Successful login.
- Invalid username/password.
- Opaque access token generation.
- Access token stored after login.
- Protected endpoint with valid access token.
- Missing bearer token.
- Invalid bearer token.
- Expired access token.
- Revoked access token.
- Logout revokes the access token.
- Revoked access token cannot be reused.
- `/users/me` returns the correct authenticated user.
- Correct role/authority mapping.

Prefer behavioural tests over tests that simply reproduce implementation details.

Use integration tests where Spring Security filter-chain behaviour needs verification.

---

## Code Quality

Although this is an experiment, code should be written to production-level quality.

Requirements:

- Clear naming.
- Small and focused methods.
- Constructor injection.
- No field injection.
- No unnecessary static state.
- No duplicated authentication logic.
- DTOs at API boundaries.
- Do not expose persistence entities directly through APIs.
- Consistent authentication failure handling.
- Explicit handling of security-sensitive operations.
- Comments should explain reasoning rather than restating code.

Avoid unnecessary abstraction and over-engineering.

The complete authentication flow should remain easy to understand from the project source.

---

## Scope Boundaries

Do not introduce the following unless specifically requested:

- JWT
- Refresh tokens
- `/auth/refresh`
- OAuth 2.0 Authorization Server
- OAuth 2.0 Client
- OpenID Connect
- SAML
- API keys
- Session-cookie authentication
- Redis
- Kafka
- Microservices

In particular:

```text
Access Token  -> YES
Refresh Token -> NO
```

When an access token expires, the user authenticates again using username and password.

The objective of this experiment is strictly:

```text
Opaque Access Token
        +
Bearer Authentication
        +
Server-side Token State
        =
Stateful Bearer Authentication
```

---

## AI Agent Instructions

When modifying this project:

1. Preserve the project's learning objective.
2. Keep authentication state server-side.
3. Do not replace opaque tokens with JWTs.
4. Do not introduce refresh tokens.
5. Do not create a refresh-token endpoint.
6. Do not introduce unrelated authentication technologies.
7. Keep the implementation simple enough to expose how opaque-token authentication works.
8. Prefer standard Spring Security extension points over custom security infrastructure.
9. Maintain production-level code quality.
10. Add or update tests when authentication behaviour changes.