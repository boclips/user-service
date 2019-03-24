package com.boclips.users.infrastructure.keycloak.metadata

import com.boclips.users.domain.model.AccountMetadata
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.UserId
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.keycloak.representations.idm.UserRepresentation
import org.mockito.Mockito

internal class KeycloakMetadataProviderTest {
    lateinit var keycloakMetadataProvider: KeycloakMetadataProvider
    lateinit var keycloakWrapperMock: KeycloakWrapper

    @BeforeEach
    fun setup() {
        keycloakWrapperMock = Mockito.mock(KeycloakWrapper::class.java)
        keycloakMetadataProvider = KeycloakMetadataProvider(
            keycloakWrapperMock
        )
    }

    @Nested
    inner class GetAllMetaData {
        @Test
        fun `can fetch all valid metadata`() {
            whenever(keycloakWrapperMock.users()).thenReturn(
                listOf(
                    UserRepresentation().apply {
                        id = "user1"
                        attributes = mapOf(
                            "subjects" to listOf("maths"),
                            "mixpanelDistinctId" to listOf("1")
                        )
                    },
                    UserRepresentation().apply {
                        id = "user2"
                        attributes = mapOf(
                            "subjects" to listOf("english"),
                            "mixpanelDistinctId" to listOf("2")
                        )
                    }
                )
            )

            val metadata = keycloakMetadataProvider.getAllMetadata(
                listOf(
                    UserId(value = "user1"),
                    UserId(value = "user2")
                )
            )

            assertThat(metadata).hasSize(2)
            assertThat(metadata[UserId(value = "user1")]).isEqualTo(
                AccountMetadata(subjects = "maths", analyticsId = AnalyticsId(value = "1"))
            )

            assertThat(metadata[UserId(value = "user2")]).isEqualTo(
                AccountMetadata(subjects = "english", analyticsId = AnalyticsId(value = "2"))
            )
        }

        @Test
        fun `handles missing metadata gracefully`() {
            whenever(keycloakWrapperMock.users()).thenReturn(
                listOf(
                    UserRepresentation().apply {
                        id = "user1"
                        attributes = mapOf(
                            "subjects" to null,
                            "mixpanelDistinctId" to listOf("2")
                        )
                    }
                )
            )

            val id = UserId(value = "user1")
            val metadata = keycloakMetadataProvider.getAllMetadata(listOf(id))
            assertThat(metadata).hasSize(1)

            assertThat(metadata[id]?.subjects).isNull()
            assertThat(metadata[id]?.analyticsId).isEqualTo(AnalyticsId(value = "2"))
        }
    }

    @Nested
    inner class GetMetaData {
        @Test
        fun `can fetch all metadata`() {
            whenever(keycloakWrapperMock.getUser(any())).thenReturn(
                UserRepresentation().apply {
                    attributes = mapOf(
                        "subjects" to listOf("maths"),
                        "mixpanelDistinctId" to listOf("2")
                    )
                }
            )

            val metadata = keycloakMetadataProvider.getMetadata(UserId(value = "irrelevant"))
            assertThat(metadata.analyticsId).isEqualTo(AnalyticsId(value = "2"))
            assertThat(metadata.subjects).isEqualTo("maths")
        }

        @Test
        fun `returns null values when attributes is null`() {
            whenever(keycloakWrapperMock.getUser(any())).thenReturn(
                UserRepresentation().apply {
                    attributes = null
                }
            )

            val metadata = keycloakMetadataProvider.getMetadata(UserId(value = "irrelevant"))
            assertThat(metadata.subjects).isNull()
            assertThat(metadata.analyticsId).isNull()
        }

        @Test
        fun `handles missing metadata gracefully`() {
            whenever(keycloakWrapperMock.getUser(any())).thenReturn(
                UserRepresentation().apply {
                    attributes = mapOf(
                        "subjects" to null,
                        "mixpanelDistinctId" to listOf("2")
                    )
                }
            )

            val metadata = keycloakMetadataProvider.getMetadata(UserId(value = "irrelevant"))
            assertThat(metadata.subjects).isNull()
            assertThat(metadata.analyticsId).isEqualTo(AnalyticsId(value = "2"))
        }
    }
}