package com.boclips.users.infrastructure.keycloakclient

class KeycloakClientFakeContractTest : ContractTest() {
    override val keycloakClient = KeycloakClientFake()
    override val keycloakTestSupport = keycloakClient
}