# user-service

[![concourse](https://concourse.devboclips.net/api/v1/pipelines/boclips/jobs/build-user-service/badge)]()

The service is in charge of maintaining user profiles. It is powering the teacher app.

One of the core competencies is to synchronize a user with various external systems such as HubSpot, MixPanel and Keycloak.

Whilst Keycloak is our authentication service, the user service is storing actual user information beyond the name.

## User Service Client

> To serve users to machines.

If you are looking to integrate with the user-service, but you don't want to handle the HTTP requests yourselves,
the user-service client is a Java  wrapper for the user service.

The client is publicly distributed using [JitPack](https://jitpack.io/#boclips/user-service).

### Releasing a new client version

Releasing a new version of the client, entails cutting a new release. The concourse pipeline has a job for just that.

This is what needs to be done:

1. Make your changes & ensure the `build-user-service` job succeeds
2. Trigger the [`cut-release-user-service-client`](https://concourse.devboclips.net/teams/main/pipelines/boclips/jobs/cut-release-user-service-client) job

## Development

Configure contract test credentials, and ktlint in intellij:
```
./setup
```

Run all tests:
```
./gradlew test
```
