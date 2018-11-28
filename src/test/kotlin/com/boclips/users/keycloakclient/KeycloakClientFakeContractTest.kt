package com.boclips.users.keycloakclient

class KeycloakClientFakeContractTest : ContractTest() {
    override val keycloakClient = KeycloakClientFake()
}