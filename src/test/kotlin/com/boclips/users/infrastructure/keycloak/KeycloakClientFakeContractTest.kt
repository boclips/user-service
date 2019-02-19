package com.boclips.users.infrastructure.keycloak

import com.boclips.users.infrastructure.keycloak.client.KeycloakClientFake

class KeycloakClientFakeContractTest : ContractTest() {
    override val keycloakClient = KeycloakClientFake()
    override val keycloakTestSupport = keycloakClient
}