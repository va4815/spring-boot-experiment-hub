# Spring Boot Experiment Hub

## Purpose

This repository contains isolated experiments for evaluating and implementing Spring Boot technologies, frameworks, architectural patterns, and engineering approaches.

Experiments should be implemented to **production-level engineering standards**.

The primary goals are:

1. Evaluate technologies and architectural approaches through working implementations.
2. Produce secure, maintainable, testable, and reliable code.
3. Follow established Java and Spring Boot engineering practices.
4. Understand operational and architectural trade-offs.
5. Create implementations that could reasonably form the basis of production code.

Although experiments are isolated, code quality should not be reduced simply because an implementation is experimental.

## Repository Structure

Experiments are organised by topic and should remain independent where practical.

Examples:

```text id="6g1oe5"
authentication/
  basic-auth/
  session-cookie/
  bearer-jwt-stateless/
  bearer-opaque-stateful/
  bearer-jwt-with-refresh/

database/
  spring-boot-liquibase/
  spring-boot-flyway/
```

Each experiment may contain its own `AGENTS.md` defining experiment-specific architecture, requirements, constraints, and technology choices.

When a local `AGENTS.md` exists, follow it in addition to this file.

## Engineering Priorities

When making technical decisions, prioritise:

1. Correctness
2. Security
3. Reliability
4. Maintainability
5. Testability
6. Observability
7. Performance
8. Operational simplicity

Do not optimise for minimal code at the expense of production quality.

Avoid unnecessary complexity, but introduce abstractions and supporting infrastructure when they provide clear production value.

## General Working Rules

- Work only within the experiment relevant to the current task.
- Do not modify unrelated experiments unless explicitly requested.
- Do not perform unrelated refactoring while completing a task.
- Preserve established project conventions unless there is a justified reason to change them.
- Follow current Java and Spring Boot conventions.
- Prefer explicit, maintainable designs over clever implementations.
- Avoid duplicated business logic.
- Keep responsibilities and architectural boundaries clear.
- Do not introduce dependencies without a concrete requirement.

When an existing implementation conflicts with production-quality practices, identify the issue before changing its architecture substantially.

## Before Making Changes

For non-trivial changes:

1. Read the relevant local `AGENTS.md`, if present.
2. Inspect the existing implementation and tests.
3. Identify the affected components and dependencies.
4. Consider security, failure cases, compatibility, and operational impact.
5. Propose an approach when multiple meaningful designs are possible.
6. Implement the requested solution.
7. Verify the resulting behaviour.

For small and unambiguous changes, avoid unnecessary planning overhead.

## Repository Exploration

Use repository context efficiently.

- Inspect the minimum set of files necessary to understand the affected behaviour.
- Start with files explicitly referenced by the task.
- Follow dependencies when required for correctness.
- Do not recursively inspect unrelated experiments.
- Prefer targeted searches over broad repository scans.
- Avoid repeatedly reading files whose relevant contents are already known.
- Use concise command output where possible.

Efficiency must not compromise correctness or safety.

## Java

Follow established modern Java practices.

- Use clear and descriptive names.
- Prefer immutable data structures where appropriate.
- Use records for immutable data carriers when suitable.
- Use interfaces where they provide meaningful boundaries, not automatically.
- Keep methods focused.
- Avoid deeply nested control flow.
- Avoid unnecessary inheritance.
- Prefer composition where appropriate.
- Handle nullability deliberately.
- Avoid returning `null` when a clearer contract is available.
- Use exceptions intentionally and consistently.
- Avoid leaking implementation details across architectural boundaries.

Do not introduce abstractions solely to satisfy a design pattern.

## Spring Boot

Follow established Spring Boot conventions.

- Prefer constructor injection.
- Keep configuration explicit and understandable.
- Use dependency injection rather than manually constructing managed components.
- Keep controllers focused on HTTP/API concerns.
- Keep business logic outside controllers.
- Keep persistence concerns outside controllers and service orchestration.
- Use configuration properties for environment-specific configuration.
- Do not hard-code secrets or environment-specific values.
- Use framework capabilities where they provide clear value rather than recreating them unnecessarily.
- Avoid unnecessary framework customisation.

Understand the behaviour and lifecycle of Spring-managed components before introducing custom mechanisms.

## API Design

For HTTP APIs:

- Use appropriate HTTP methods and status codes.
- Validate request input.
- Return consistent error responses.
- Separate API models from persistence models where appropriate.
- Avoid exposing sensitive implementation details.
- Maintain clear request and response contracts.
- Consider backward compatibility when changing existing endpoints.
- Use consistent naming and endpoint conventions.

Do not expose stack traces, internal exception messages, secrets, or sensitive security information through API responses.

## Security

Treat security as a first-class requirement.

For authentication and authorisation experiments:

- Follow current Spring Security practices.
- Apply least privilege.
- Validate authentication and authorisation independently where appropriate.
- Store passwords only using appropriate password hashing mechanisms.
- Never log passwords, access tokens, refresh tokens, API keys, OTP secrets, or other credentials.
- Avoid hard-coded credentials and secrets.
- Validate token signatures, expiry, issuer, audience, and other relevant claims where applicable.
- Consider credential and token rotation.
- Consider revocation requirements.
- Consider replay attacks where relevant.
- Consider brute-force and abuse scenarios.
- Consider CSRF when authentication relies on browser-managed credentials.
- Configure CORS deliberately rather than disabling protections without justification.
- Use secure cookie attributes where cookies are involved.
- Fail securely when authentication information is missing, malformed, expired, or invalid.

Do not disable security controls merely to make an experiment easier to run.

If an experiment intentionally demonstrates an insecure mechanism, clearly isolate and document that limitation.

## Persistence

When persistence is involved:

- Use clear transactional boundaries.
- Maintain data integrity using appropriate database constraints.
- Use indexes where justified by access patterns.
- Avoid unnecessary database queries.
- Consider concurrency and race conditions.
- Avoid N+1 query behaviour.
- Do not rely solely on application validation for database invariants.
- Use database migrations for schema changes.
- Do not modify an existing applied migration unless the experiment explicitly permits it.

Consider rollback, migration compatibility, and deployment implications where relevant.

## Error Handling

Handle failures deliberately.

- Validate expected failure scenarios.
- Use appropriate exception types.
- Map application failures to suitable API responses.
- Avoid swallowing exceptions.
- Avoid broad exception handling without justification.
- Preserve useful diagnostic information internally.
- Do not expose sensitive internal information externally.

Failure behaviour should be testable and predictable.

## Logging and Observability

Production-oriented implementations should provide useful operational visibility.

- Use structured and meaningful log messages.
- Use appropriate log levels.
- Do not log secrets or sensitive credentials.
- Avoid unnecessary high-volume logging.
- Include sufficient context to investigate failures.
- Consider metrics and health indicators when operationally relevant.
- Prefer Spring Boot Actuator capabilities where appropriate instead of custom health mechanisms.

Observability should assist diagnosis without compromising security.

## Testing

Tests are part of the implementation, not an optional follow-up.

Use appropriate combinations of:

- unit tests
- integration tests
- Spring context tests
- repository tests
- security tests
- API tests

Tests should cover:

- expected behaviour
- important failure scenarios
- validation
- security boundaries
- edge cases
- regression-prone behaviour

For authentication mechanisms, include negative security cases such as missing, malformed, invalid, expired, or unauthorised credentials where applicable.

Prefer behaviour-focused tests over tests that merely reproduce implementation details.

## Test Execution

Run the narrowest relevant verification first.

For example:

```bash id="md73vz"
./mvnw -Dtest=SomeTest test
```

Then expand verification when the scope of the change warrants it.

Before considering a substantial implementation complete, run the appropriate module-level test suite or build.

Do not repeatedly run unrelated repository-wide tests.

## Dependencies

Before adding a dependency:

1. Determine whether the existing stack already provides the required capability.
2. Verify that the dependency has a clear purpose.
3. Prefer mature and actively maintained libraries.
4. Avoid overlapping libraries that solve the same problem.
5. Keep dependency scope as narrow as possible.

Do not introduce a library for functionality that can be implemented safely and clearly using the existing stack without significant maintenance cost.

## Configuration and Secrets

- Never commit real secrets.
- Use environment variables or external configuration for secrets.
- Provide safe development defaults only when appropriate.
- Separate environment-specific configuration from application logic.
- Document required configuration.
- Do not expose credentials in logs, test output, or documentation.

Example credentials must be clearly non-production credentials.

## Code Changes

Keep changes focused and reviewable.

After making changes:

1. Review affected files.
2. Check for unintended changes.
3. Run relevant tests.
4. Run broader verification when warranted.
5. Report what changed.
6. Report verification performed.
7. Identify important assumptions or unresolved risks.

Do not perform unrelated cleanup unless explicitly requested.

## Git

- Do not commit unless explicitly requested.
- Do not push unless explicitly requested.
- Do not rewrite Git history.
- Do not discard existing user changes.
- Treat existing uncommitted changes as intentional unless instructed otherwise.
- Keep changes focused enough to be reviewed easily.

## Documentation

`README.md` files should document the experiment for developers.

Where relevant, include:

- objective
- architecture
- important design decisions
- setup
- configuration
- execution
- API usage
- security characteristics
- testing
- operational considerations
- limitations
- trade-offs

Documentation should describe the actual implementation rather than an idealised architecture that differs from the code.

## Experiment-Specific Instructions

Technology-specific requirements belong in local `AGENTS.md` files.

Examples:

```text id="2hhv2g"
authentication/bearer-jwt-stateless/AGENTS.md
authentication/bearer-opaque-stateful/AGENTS.md
database/spring-boot-liquibase/AGENTS.md
```

A local file should define matters such as:

- experiment objective
- architecture
- authentication model
- stateful/stateless requirements
- permitted technologies
- intentionally excluded technologies
- expected behaviour
- security assumptions
- experiment-specific testing requirements

Do not place every experiment's technical decisions in this root file.

## Default Workflow

Unless instructed otherwise:

```text id="p1x0u3"
Understand requirement
        ↓
Read applicable AGENTS.md
        ↓
Inspect relevant implementation
        ↓
Identify risks and dependencies
        ↓
Propose design when necessary
        ↓
Implement focused change
        ↓
Run targeted tests
        ↓
Run appropriate broader verification
        ↓
Review implementation
        ↓
Report result and remaining risks
```

The target is not merely code that works.

The target is code that is **correct, secure, maintainable, testable, operationally understandable, and suitable as the basis of a production system**.