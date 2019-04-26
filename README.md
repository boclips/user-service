# user-service

[![concourse](https://concourse.devboclips.net/api/v1/pipelines/boclips/jobs/build-user-service/badge)]()

The service is in charge of maintaining user profiles. It is powering the teacher app.

One of the core competencies is to synchronize a user with various external systems such as HubSpot, MixPanel and Keycloak.

Whilst Keycloak is our authentication service, the user service is storing actual user information beyond the name.

## Development

Configure contract test credentials, and ktlint in intellij:
```
./setup
```

Run all tests:
```
./gradlew test
```
