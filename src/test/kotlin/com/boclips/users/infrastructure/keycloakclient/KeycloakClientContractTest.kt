package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.infrastructure.keycloakclient.KeycloakClient.Companion.REALM
import com.boclips.users.testsupport.KeycloakTestSupport
import org.keycloak.admin.client.Keycloak
import org.yaml.snakeyaml.Yaml
import java.io.InputStream

class KeycloakClientContractTest : ContractTest() {
    private val keycloakInstance: Keycloak = Keycloak.getInstance(
        "https://login.testing-boclips.com/auth",
        REALM,
        readSecret("KEYCLOAK_USERNAME"),
        readSecret("KEYCLOAK_PASSWORD"),
        "admin-cli"
    )

    override val keycloakClient: KeycloakClient = KeycloakClient(keycloakInstance)

    override val keycloakTestSupport: LowLevelKeycloakClient = KeycloakTestSupport(
        keycloakInstance,
        keycloakClient
    )

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
