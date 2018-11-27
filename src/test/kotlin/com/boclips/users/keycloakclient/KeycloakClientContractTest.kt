package com.boclips.users.keycloakclient

import org.assertj.core.api.Assertions.assertThat
import org.bouncycastle.cms.RecipientId.password
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.util.stream.Stream

class KeycloakClientContractTest {

    @ParameterizedTest
    @ArgumentsSource(PlaybackProviderArgumentProvider::class)
    fun `getUser`(keycloakClient: IdentityProvider) {
        val user: KeycloakUser = keycloakClient.getUser("b8dba3ac-c5a2-453e-b3d6-b1af1e48f027")

        assertThat(user.id).isEqualTo("b8dba3ac-c5a2-453e-b3d6-b1af1e48f027")
        assertThat(user.username).isEqualTo("boclipper")
        assertThat(user.firstName).isEqualTo("Little")
        assertThat(user.lastName).isEqualTo("Bo")
        assertThat(user.email).isEqualTo("engineering@boclips.com")
    }

}

class PlaybackProviderArgumentProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {

        return Stream.of(
                KeycloakClient(KeycloakConfig(
                        url = "https://login.testing-boclips.com/auth",
                        username = readSecret("KEYCLOAK_USERNAME"),
                        password = readSecret("KEYCLOAK_PASSWORD")
                )),
                KeycloakClientFake()
        ).map { playbackProvider -> Arguments.of(playbackProvider) }
    }
}

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
