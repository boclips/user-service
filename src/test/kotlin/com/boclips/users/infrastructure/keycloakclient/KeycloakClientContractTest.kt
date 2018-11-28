package com.boclips.users.infrastructure.keycloakclient

import org.yaml.snakeyaml.Yaml
import java.io.InputStream

class KeycloakClientContractTest : ContractTest() {
    override val keycloakClient: KeycloakClient = KeycloakClient(KeycloakConfig(
            url = "https://login.testing-boclips.com/auth",
            username = readSecret("KEYCLOAK_USERNAME"),
            password = readSecret("KEYCLOAK_PASSWORD")
    ))

    private fun readSecret(key: String): String {
        if (System.getenv(key) != null) {
            return System.getenv(key)
        }

        val yaml = Yaml()
        val inputStream: InputStream =
                KeycloakClientContractTest::javaClass.javaClass.classLoader
                        .getResourceAsStream("contract-test-setup.yml")

        val apiKey = yaml.load<Map<String, String>>(inputStream)[key]!!
        inputStream.close()

        return apiKey
    }
}
