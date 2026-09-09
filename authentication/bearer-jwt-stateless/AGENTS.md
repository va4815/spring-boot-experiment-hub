# AGENTS.md

## Project Purpose

This project is an authentication experiment demonstrating:

- Bearer token authentication
- JWT access tokens
- Stateless authentication
- Spring Security integration
- Role-based authorisation
- Authentication using database-backed users

The primary goal is to understand how stateless JWT authentication works in a Spring Boot application and how it differs from:

- Basic Authentication
- Session + Cookie authentication
- Opaque bearer tokens
- JWT access token + refresh token authentication
- OAuth 2.0 / OpenID Connect

This is an educational experiment, but code quality should be suitable for a production-style Spring Boot project.

---

## Scope

Implement only the functionality required to demonstrate stateless JWT authentication.

Expected authentication flow:

```text
Client
  |
  | POST /auth/login
  | username + password
  v
Application
  |
  | authenticate credentials
  v
AuthenticationManager
  |
  | UserDetailsService
  v
Database
  |
  | valid credentials
  v
JWT generated
  |
  v
Client receives access token
  |
  | Authorization: Bearer <jwt>
  v
JWT authentication filter
  |
  | validate JWT
  | extract user identity
  | create Authentication
  v
SecurityContext
  |
  v
Protected API
```

The application must not use server-side HTTP sessions to maintain authentication state.

---

## Authentication Model

This project uses:

```text
Bearer Token
+
JWT Access Token
+
Stateless Authentication
```

The JWT itself contains the information required to identify the authenticated user.

The server validates the JWT on every protected request.

There is no session lookup or token lookup for normal authenticated requests.

---

## Stateless Requirement

Spring Security must be configured with:

```java
SessionCreationPolicy.STATELESS
```

Do not introduce:

- `HttpSession`
- `JSESSIONID`
- Spring Session
- Redis-backed sessions
- database-backed sessions
- server-side login session state

Authentication must be reconstructed from the JWT on every request.

---

## Authentication Flow

### Login

The client sends credentials to:

```http
POST /auth/login
```

Example request:

```json
{
  "username": "admin",
  "password": "password"
}
```

The application must authenticate credentials using Spring Security's authentication infrastructure.

Preferred flow:

```text
AuthController
    |
    v
AuthenticationManager
    |
    v
DaoAuthenticationProvider
    |
    v
UserDetailsService
    |
    v
PasswordEncoder
```

Do not manually compare raw passwords.

After successful authentication, generate and return a JWT access token.

Example response shape:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

The exact DTO names may differ depending on the existing project structure.

---

## Authenticated Requests

Clients must send the JWT using:

```http
Authorization: Bearer <access-token>
```

The application must:

1. Extract the bearer token from the `Authorization` header.
2. Validate the JWT signature.
3. Validate token expiration.
4. Extract the user identity.
5. Reconstruct an authenticated Spring Security `Authentication`.
6. Store the authentication in `SecurityContextHolder`.
7. Continue the filter chain.

Invalid or expired tokens must not authenticate the request.

---

## JWT Responsibilities

JWT generation and parsing should be isolated from controllers.

Use a dedicated component/service such as:

```text
JwtService
```

or:

```text
JwtTokenService
```

It should be responsible for:

- generating JWTs
- signing JWTs
- validating signatures
- reading claims
- reading subject
- reading expiration
- checking token expiration

Do not place JWT implementation logic directly inside controllers.

---

## JWT Claims

Keep JWT claims minimal.

Recommended claims:

```text
sub   -> stable user identifier or username
iat   -> issued-at timestamp
exp   -> expiration timestamp
```

Role or authority information may be included for this experiment if useful.

Example conceptual payload:

```json
{
  "sub": "admin",
  "roles": [
    "SHOP_MANAGER"
  ],
  "iat": 1788970000,
  "exp": 1788970900
}
```

Do not include sensitive information such as:

- password
- password hash
- personal secrets
- database credentials
- authentication secrets

Remember that JWT payloads are encoded, not encrypted by default.

---

## JWT Lifetime

Access tokens should be short-lived.

For this experiment, a reasonable default is approximately:

```text
15 minutes
```

Avoid creating effectively permanent access tokens.

Token lifetime should be configurable through application configuration rather than hard-coded throughout the codebase.

Example:

```yaml
security:
  jwt:
    access-token-expiration: 15m
```

Use the configuration style already adopted by the project where possible.

---

## Signing

JWTs must be cryptographically signed.

Do not:

- create unsigned JWTs
- accept `alg: none`
- store signing secrets directly in source code intended for production-style implementation

For local experimentation, configuration may provide a development secret.

Prefer configuration such as:

```yaml
security:
  jwt:
    secret: ${JWT_SECRET}
```

If asymmetric signing is introduced, keep signing and verification responsibilities clearly separated.

Do not introduce asymmetric signing unless it materially contributes to this experiment.

---

## JWT Filter

Use a dedicated Spring Security filter for bearer JWT authentication where appropriate.

A typical implementation may extend:

```java
OncePerRequestFilter
```

Responsibilities should remain narrow:

```text
read Authorization header
    ↓
extract bearer token
    ↓
validate token
    ↓
extract identity
    ↓
construct Authentication
    ↓
set SecurityContext
    ↓
continue filter chain
```

The filter should not:

- perform login
- generate access tokens
- contain controller logic
- contain database business logic
- return application-specific successful responses

---

## User Lookup Strategy

The login flow must load users from the database using the project's custom:

```java
UserDetailsService
```

For authenticated JWT requests, choose one strategy deliberately.

### Strategy A — Database lookup on every JWT request

```text
JWT
 ↓
extract username
 ↓
UserDetailsService
 ↓
database
 ↓
construct Authentication
```

Advantages:

- current user status and role can be checked
- role changes take effect immediately

Disadvantages:

- introduces database access on every authenticated request
- reduces some benefits of stateless JWT authentication

### Strategy B — Authorities stored in JWT

```text
JWT
 ↓
extract username + authorities
 ↓
construct Authentication directly
```

Advantages:

- no database lookup for every request
- fully self-contained access token

Disadvantages:

- role changes may not take effect until token expiration

For this experiment, either strategy is acceptable.

Document which strategy is being used and why.

Do not accidentally mix both strategies without justification.

---

## Roles and Authorities

The database role model may contain values such as:

```text
SHOP_MANAGER
STAFF
```

Do not rename stored database role codes merely because Spring Security internally uses `ROLE_` conventions.

Convert database roles to Spring Security authorities at the application boundary when necessary.

For example:

```java
new SimpleGrantedAuthority("ROLE_" + roleCode)
```

Database:

```text
SHOP_MANAGER
```

Spring Security authority:

```text
ROLE_SHOP_MANAGER
```

Keep persistence terminology independent from framework-specific prefixes.

---

## Security Configuration

The Spring Security configuration should clearly express the authentication model.

Expected characteristics:

```text
SessionCreationPolicy.STATELESS
```

Public endpoints may include:

```text
POST /auth/login
```

Protected endpoints require authentication.

Example conceptual configuration:

```java
http
    .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    )
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/login").permitAll()
        .anyRequest().authenticated()
    );
```

The final implementation should follow the Spring Security version used by the project.

Avoid deprecated APIs.

---

## CSRF

This project uses bearer JWT authentication through the:

```http
Authorization
```

header rather than browser-managed authentication cookies.

Therefore, traditional CSRF protection is generally not required for APIs authenticated exclusively through bearer tokens in headers.

If CSRF is disabled, document the reason.

Do not conclude that:

```text
JWT = always disable CSRF
```

The relevant distinction is whether authentication credentials are automatically attached by the browser.

If future changes store authentication credentials in cookies, reassess the CSRF model.

---

## Logout Behaviour

This experiment uses stateless JWT access tokens.

Therefore:

```http
POST /auth/logout
```

cannot inherently invalidate an already-issued access token unless server-side token state is introduced.

For this experiment, logout may simply mean:

```text
client deletes/discards access token
```

Do not introduce a token blacklist, revocation database, or Redis token store unless explicitly required by the experiment.

This limitation is intentional and should be documented.

A later experiment such as:

```text
bearer-jwt-with-refresh
```

can explore stronger token lifecycle management.

---

## Refresh Tokens

Refresh tokens are explicitly outside the scope of this project.

Do not implement:

- refresh token issuance
- `/auth/refresh`
- refresh token rotation
- refresh token database tables
- refresh token revocation

Those concepts belong in the separate:

```text
bearer-jwt-with-refresh
```

experiment.

---

## OAuth 2.0 and OIDC

Do not introduce:

- OAuth 2.0 authorization servers
- OAuth 2.0 clients
- OpenID Connect
- ID tokens
- PKCE
- client credentials flow
- external identity providers

This project demonstrates application-managed username/password authentication followed by issuance of a JWT bearer access token.

It is not an OAuth 2.0 implementation.

---

## API Keys

Do not implement API key authentication in this project.

API key authentication belongs to its own experiment.

---

## Persistence

Keep the existing authentication persistence model simple.

Expected entities may include:

```text
role
user_account
```

Example relationship:

```text
role
 ├── id
 ├── code
 └── name

user_account
 ├── id
 ├── username
 ├── password
 └── role_id
```

Passwords must be stored as password hashes.

Use:

```java
PasswordEncoder
```

Prefer BCrypt unless the project deliberately evaluates another password hashing algorithm.

Never store plaintext passwords.

---

## Package Structure

Prefer clear separation of responsibilities.

A possible structure is:

```text
src/main/java/...

authentication/
├── controller/
│   └── AuthController
├── dto/
│   ├── LoginRequest
│   └── LoginResponse
├── security/
│   ├── SecurityConfig
│   ├── JwtAuthenticationFilter
│   ├── JwtService
│   └── AuthUserDetails
├── service/
│   └── AuthUserDetailsService
└── repository/
    └── UserAccountRepository
```

Follow the existing project structure if one already exists.

Do not reorganise packages unnecessarily merely to match this example.

---

## Controller Responsibilities

Controllers should handle HTTP concerns only.

`AuthController` may:

- accept login credentials
- call authentication services
- return token response DTOs

Controllers should not:

- validate JWT signatures
- hash passwords
- query authentication tables directly
- manually compare passwords
- configure Spring Security

Protected controllers should obtain the authenticated user through Spring Security rather than parsing JWTs themselves.

For example:

```java
Authentication authentication
```

or:

```java
@AuthenticationPrincipal
```

may be used where appropriate.

Do not manually decode the `Authorization` header inside normal application controllers.

---

## Error Handling

Authentication failures must return appropriate HTTP responses.

Typical behaviour:

```text
Invalid credentials       -> 401 Unauthorized
Missing bearer token      -> 401 Unauthorized
Invalid JWT               -> 401 Unauthorized
Expired JWT               -> 401 Unauthorized
Authenticated but denied  -> 403 Forbidden
```

Do not leak detailed security information such as:

```text
username exists but password was incorrect
```

Prefer generic authentication failure responses.

---

## Testing Expectations

Tests should cover the authentication flow rather than only individual methods.

### Login tests

Cover:

```text
valid username/password
invalid password
unknown username
successful JWT generation
```

### JWT validation tests

Cover:

```text
valid token
expired token
invalid signature
malformed token
```

### Protected endpoint tests

Cover:

```text
request without token -> 401
request with valid token -> success
request with invalid token -> 401
request with expired token -> 401
```

### Authorisation tests

If roles are used, test:

```text
allowed role -> success
disallowed role -> 403
```

Prefer:

- JUnit 5
- Spring Boot Test
- Spring Security Test
- Mockito where isolation adds value

Do not mock security behaviour excessively when an integration-style Spring Security test would better demonstrate the real authentication flow.

---

## Mockito

Use Mockito only when it improves test isolation.

Do not mock:

- JWT parsing when testing actual JWT validation
- Spring Security's entire filter chain when testing authentication integration
- core framework behaviour merely to increase test coverage

Prefer realistic security tests where practical.

If the project uses:

```text
mock-maker-subclass
```

retain it unless there is a deliberate reason to change Mockito's mock maker configuration.

---

## Code Quality

Code should follow production-style standards even though this is an experiment.

Prefer:

- constructor injection
- immutable DTOs where practical
- Java records for simple DTOs where appropriate
- small cohesive classes
- explicit naming
- clear separation of security responsibilities
- meaningful tests
- configuration externalisation
- structured exception handling

Avoid:

- field injection
- duplicated authentication logic
- static mutable state
- oversized controllers
- hard-coded credentials
- hard-coded production secrets
- deprecated Spring Security APIs
- unnecessary abstractions

---

## Dependency Management

Use existing project dependencies wherever possible.

Before adding a dependency:

1. Check whether the functionality is already available.
2. Confirm compatibility with the current Spring Boot version.
3. Prefer widely adopted and actively maintained libraries.
4. Avoid introducing libraries solely to save a few lines of code.

For JWT implementation, use the library already selected by the project.

Do not replace the JWT library without a concrete reason.

---

## Configuration

Security-sensitive values should be externalised.

Examples include:

```text
JWT signing secret
access-token lifetime
issuer
```

Avoid spreading configuration constants throughout the application.

Prefer typed configuration properties when configuration grows beyond a trivial size.

---

## Logging

Authentication-related logs must not expose:

- passwords
- JWT signing secrets
- complete access tokens
- password hashes

Useful logs may include:

```text
authentication failure
token validation failure category
username or user ID where appropriate
access denied
```

Do not log raw credentials.

---

## Documentation

When introducing or modifying authentication behaviour, update the project documentation when relevant.

The documentation should explain:

```text
how login works
how the JWT is issued
how the JWT is sent
how authentication is reconstructed
why the project is stateless
how expiration works
what logout means
what the limitations are
```

The experiment should make the architectural trade-offs clear, not merely demonstrate code that happens to work.

---

## Architectural Questions This Experiment Should Answer

While working on this project, preserve enough clarity in the implementation and documentation to answer:

1. What does `Bearer` mean?
2. What makes JWT different from an opaque token?
3. Why is this implementation stateless?
4. How does Spring Security authenticate a login request?
5. How does Spring Security authenticate later requests without a session?
6. What information should be stored inside a JWT?
7. How is a JWT signature validated?
8. What happens when a JWT expires?
9. Why can an issued stateless JWT not easily be revoked?
10. What happens when a user's role changes while a JWT is still valid?
11. Why is a short access-token lifetime important?
12. Why does bearer-token authentication normally not require CSRF protection when the token is sent explicitly in the `Authorization` header?
13. What is the difference between authentication and authorisation?
14. Why is this implementation not OAuth 2.0?
15. When would an opaque token be preferable to a JWT?

---

## Out of Scope

Unless explicitly requested, do not add:

```text
Refresh tokens
OAuth 2.0
OIDC
SSO
SAML
API keys
OTP
MFA
Redis
Session storage
Token blacklist
JWT revocation store
Device management
Remember-me authentication
Social login
External identity providers
Authorization Server
Microservices
Kafka
Docker infrastructure unrelated to the experiment
```

Keep this experiment focused.

---

## Agent Behaviour

When modifying this project:

1. Read the existing implementation before changing code.
2. Preserve existing naming and package conventions where reasonable.
3. Make the smallest coherent change required.
4. Explain security-relevant design decisions.
5. Do not silently weaken security to make tests pass.
6. Do not disable Spring Security features without documenting why.
7. Do not add unrelated authentication mechanisms.
8. Do not introduce refresh tokens into this experiment.
9. Do not convert this into an OAuth 2.0 project.
10. Prefer framework-standard Spring Security mechanisms over custom authentication infrastructure.
11. Add or update tests for behaviour changes.
12. Keep the experiment understandable enough to compare later with other authentication experiments.

When multiple approaches are reasonable, explain the trade-off before introducing substantial architectural complexity.

---

## Definition of Done

A basic version of this experiment is complete when:

- users are loaded from the database
- passwords are verified using `PasswordEncoder`
- `/auth/login` authenticates credentials
- successful login returns a signed JWT access token
- the JWT contains an expiration time
- protected endpoints accept `Authorization: Bearer <token>`
- the application validates the JWT for every protected request
- Spring Security reconstructs the authenticated principal
- HTTP sessions are disabled through stateless session management
- missing, invalid, and expired tokens return `401`
- role-based authorisation works where implemented
- tests cover login and protected endpoint behaviour
- the README explains the authentication flow
- the project clearly documents the limitations of stateless JWT access tokens