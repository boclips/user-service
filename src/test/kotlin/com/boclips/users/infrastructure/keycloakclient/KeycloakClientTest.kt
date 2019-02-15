package com.boclips.users.infrastructure.keycloakclient

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.keycloak.representations.idm.EventRepresentation
import org.mockito.Mockito
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

internal class KeycloakClientTest {

    var keycloakClient: KeycloakClient = KeycloakClient(KeycloakProperties().apply {
        url = "irrelevant"
        username = "irrelevant"
        password = "irrelevant"
    })

    @Test
    fun `can filter register events by time`() {
        val spyKeyCloakClient = Mockito.spy(keycloakClient)
        val now = LocalDateTime.now()
        doReturn(
            listOf(
                EventRepresentation().also {
                    it.time = now.minus(Duration.ofHours(1)).toInstant(ZoneOffset.UTC).toEpochMilli()
                    it.userId = "oneHourAgo"
                }, EventRepresentation().also {
                    it.time = now.minus(Duration.ofHours(2)).toInstant(ZoneOffset.UTC).toEpochMilli()
                    it.userId = "twoHoursAgo"
                }, EventRepresentation().also {
                    it.time = now.minus(Duration.ofHours(3)).toInstant(ZoneOffset.UTC).toEpochMilli()
                    it.userId = "threeHoursAgo"
                }
            )
        ).whenever(spyKeyCloakClient).getRegisterEvents(any())

        val twoAndAHalfHoursAgo = now.minus(Duration.ofHours(2)).minus(Duration.ofMinutes(30))
        val userIds = spyKeyCloakClient.getUserIdsRegisteredSince(twoAndAHalfHoursAgo)

        assertThat(userIds.map { it.value }).containsExactly("oneHourAgo", "twoHoursAgo")
    }
}