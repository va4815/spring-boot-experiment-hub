# AGENTS.md

## Project Overview

This project is part of the `spring-boot-experiment-hub` repository.

The purpose of this experiment is to implement and understand a hybrid Bearer Token authentication model using:

- **JWT access token — stateless**
- **Opaque refresh token — stateful**

The experiment should demonstrate the complete authentication lifecycle, including login, access-token validation, token refresh, logout/revocation, and refresh-token persistence.

This is a learning and experimentation project, but the implementation should follow production-quality Spring Boot engineering practices where practical.

---

## Authentication Model

### Access Token

Use a **JWT access token**.

Characteristics:

- Sent using the HTTP `Authorization` header:

  `Authorization: Bearer <access-token>`

- Short-lived.
- Cryptographically signed.
- Validated without querying a token store.
- Authentication is **stateless**.
- The server must not persist individual JWT access tokens.
- Protected API requests should not create or depend on an HTTP session.

The JWT should contain only the claims required for authentication and authorisation.

Possible claims include:

- `sub` — user identifier
- `iss` — token issuer
- `iat` — issued-at time
- `exp` — expiration time
- roles or authorities where appropriate

Do not place passwords, sensitive personal information, or unnecessary application data inside the JWT.

### Refresh Token

Use an **opaque refresh token**.

Characteristics:

- Cryptographically secure random value.
- Long-lived compared with the access token.
- Has no client-readable authentication claims.
- Stored and managed by the server.
- Authentication lifecycle is therefore **stateful** for refresh tokens.
- Can be revoked independently of the JWT access token.
- Used only to obtain a new access token.

The refresh-token store should support determining whether a refresh token:

- exists
- belongs to the expected user
- has expired
- has been revoked

Do not implement the refresh token as another JWT.

---

## Expected Authentication Flow

### Login

The client submits valid credentials.

The server:

1. authenticates the user;
2. generates a short-lived JWT access token;
3. generates a secure opaque refresh token;
4. persists the refresh-token state;
5. returns the access token and refresh token.

Example conceptual response:

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<opaque-token>",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

Do not expose internal refresh-token database information.

### Access Protected API

The client sends:

```text
Authorization: Bearer <jwt-access-token>
```

Spring Security validates the JWT.

A valid JWT should establish the authenticated `SecurityContext` without querying the refresh-token store.

The refresh token must not be required when accessing normal protected APIs.

### Refresh Access Token

The client submits its opaque refresh token to the refresh endpoint.

The server:

1. locates the refresh-token state;
2. verifies that the token is valid;
3. verifies that it has not expired;
4. verifies that it has not been revoked;
5. generates a new JWT access token;
6. returns the new token information.

Keep refresh-token validation separate from JWT access-token validation.

### Logout / Revocation

Logout should invalidate the relevant refresh token or tokens.

Existing JWT access tokens cannot normally be immediately invalidated because they are stateless.

They remain valid until expiration unless a separate JWT revocation mechanism is deliberately introduced.

For this experiment, prefer short-lived JWT access tokens rather than introducing a JWT blacklist.

---

## Security Requirements

Configure Spring Security for stateless API authentication.

Expected configuration:

```java
SessionCreationPolicy.STATELESS
```

Do not use:

- HTTP sessions for authenticated API state
- `JSESSIONID`
- server-side JWT access-token storage
- JWT access-token database lookup on every request

JWT validation should verify, as appropriate:

- signature
- expiration
- issuer
- required claims

Refresh tokens must be generated using a cryptographically secure random source.

Do not log:

- passwords
- raw access tokens
- raw refresh tokens
- signing private keys or secrets

Passwords must be stored using an appropriate `PasswordEncoder`, such as BCrypt.

---

## Refresh Token Persistence

Create a dedicated persistence model for refresh-token state.

A refresh-token record should contain only the fields required for lifecycle management, for example:

```text
id
user_id
token_hash
expires_at
created_at
revoked_at
```

Prefer storing a **hash of the refresh token** rather than the raw refresh-token value.

The raw token is returned to the client, while the server persists its hash.

When a refresh request is received:

```text
raw refresh token
        ↓
      hash
        ↓
compare / lookup persisted token hash
```

Do not persist JWT access tokens.

---

## Suggested API Endpoints

The experiment may expose endpoints such as:

```text
POST /auth/login
POST /auth/refresh
POST /auth/logout

GET /users/me
GET /public/**
```

Expected access rules:

```text
/auth/login      -> public
/auth/refresh    -> public endpoint with refresh-token validation
/auth/logout     -> authentication/revocation handling
/public/**       -> public
/users/me        -> valid JWT access token required
```

Do not treat possession of a refresh token as normal API authentication.

---

## Architecture Responsibilities

Keep responsibilities separated.

### Authentication

Responsible for:

- credential authentication
- login workflow

### JWT Access Token

Responsible for:

- JWT generation
- JWT claims
- JWT signing
- JWT validation
- JWT expiration

### Refresh Token

Responsible for:

- secure token generation
- persistence
- lookup
- expiration
- revocation
- refresh lifecycle

Avoid combining all authentication logic into the controller or a single large service.

Controllers should primarily handle HTTP request/response concerns and delegate authentication logic to services.

---

## Spring Security

Prefer Spring Security's standard mechanisms where appropriate.

Use Spring Security's Resource Server support for Bearer JWT authentication when practical rather than manually parsing the `Authorization` header in controllers.

Authentication information should be obtained through the Spring Security context rather than manually decoding the JWT inside each controller.

Avoid implementing custom security filters unless the experiment specifically requires behaviour that cannot reasonably be implemented using standard Spring Security components.

---

## Testing Requirements

Tests should cover the important authentication lifecycle.

### Login

Test:

- successful authentication
- invalid username
- invalid password
- access-token generation
- refresh-token generation
- refresh-token persistence

### JWT Access Token

Test:

- valid JWT
- expired JWT
- malformed JWT
- invalid signature
- missing Bearer token
- protected endpoint without authentication

### Refresh Token

Test:

- valid refresh token
- unknown refresh token
- expired refresh token
- revoked refresh token
- generation of a new access token

### Logout

Test:

- refresh-token revocation
- reuse of revoked refresh token is rejected

Prefer focused unit tests for token/service logic and integration tests for important Spring Security flows.

---

## Code Quality

Follow production-level Java and Spring Boot conventions.

Prefer:

- clear package boundaries
- constructor injection
- immutable DTOs where practical
- Java records for suitable request/response models
- small focused services
- explicit domain terminology
- meaningful method and variable names
- centralised exception handling where appropriate

Avoid:

- field injection
- duplicated security logic
- unnecessary abstractions
- hard-coded credentials
- hard-coded signing keys
- business logic inside controllers
- manually parsing JWTs throughout the application

Keep the implementation simple enough that the authentication lifecycle remains easy to understand.

---

## Configuration

Security-sensitive configuration should come from configuration properties or environment variables.

Examples:

```text
JWT_ISSUER
JWT_ACCESS_TOKEN_EXPIRATION
JWT_PRIVATE_KEY
JWT_PUBLIC_KEY
REFRESH_TOKEN_EXPIRATION
```

Do not commit production secrets, private keys, passwords, or real credentials to the repository.

Development-only configuration should be clearly identifiable as development configuration.

---

## Scope

The primary scope of this experiment is:

```text
Credentials
    ↓
Authentication
    ↓
JWT Access Token
    ↓
Stateless API Authentication

        +

Opaque Refresh Token
    ↓
Persistent Token State
    ↓
Refresh / Revocation
```

Focus on understanding the boundary between:

```text
JWT access token
= stateless authentication

Opaque refresh token
= stateful token lifecycle management
```

Do not unnecessarily expand this experiment into:

- OAuth 2.0 Authorization Server implementation
- OpenID Connect
- SSO
- API keys
- OTP
- distributed session management

Those concerns belong to separate experiments in `spring-boot-experiment-hub`.

---

## Agent Instructions

When modifying this project:

1. Preserve the hybrid authentication architecture:
    - JWT access token = stateless
    - opaque refresh token = stateful.

2. Do not introduce HTTP sessions.

3. Do not persist JWT access tokens.

4. Do not convert the opaque refresh token into a JWT.

5. Do not introduce a JWT blacklist unless explicitly requested.

6. Keep access-token validation independent from refresh-token persistence.

7. Prefer standard Spring Security mechanisms over custom authentication infrastructure.

8. Explain significant security or architectural changes before introducing them.

9. Keep implementations focused on the purpose of this experiment.

10. Add or update tests when authentication behaviour changes.

11. Do not introduce unrelated frameworks, dependencies, or architectural patterns without a clear requirement.

12. Never weaken security controls merely to make a test or endpoint work.

The goal is not only to make authentication work, but to make the distinction between **stateless JWT access authentication** and **stateful opaque refresh-token management** explicit in the design and implementation.