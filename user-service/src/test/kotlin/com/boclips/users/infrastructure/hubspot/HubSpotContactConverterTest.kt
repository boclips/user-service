package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.subject.SubjectId
import com.boclips.users.testsupport.factories.CrmProfileFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant

class HubSpotContactConverterTest {

    @Test
    fun `subjects are stored as comma separated string`() {
        val hubSpotContact =
            HubSpotContactConverter().convert(
                CrmProfileFactory.sample(
                    subjects = listOf(
                        Subject(
                            id = SubjectId(
                                value = "1"
                            ), name = "Maths"
                        ),
                        Subject(
                            id = SubjectId(
                                value = "2"
                            ), name = "Biology"
                        )
                    )
                )
            )

        assertThat(hubSpotContact.properties.first { it.property == "subjects_taught" }.value).isEqualTo("Maths, Biology")
    }

    @Test
    fun `age ranges are stored as comma separated string`() {
        val hubSpotContact = HubSpotContactConverter().convert(
            CrmProfileFactory.sample(ageRanges = listOf(8, 9, 10))
        )

        assertThat(hubSpotContact.properties.first { it.property == "age_range" }.value).isEqualTo("8, 9, 10")
    }

    @Test
    fun `last logged in is an instant`() {
        val hubSpotContact = HubSpotContactConverter().convert(
            CrmProfileFactory.sample(
                lastLoggedIn = Instant.parse("2018-01-01T00:00:00.123Z")
            )
        )

        val lastLoggedIn = hubSpotContact.properties.first { it.property == "b2t_last_logged_in" }
        assertThat(lastLoggedIn.value).isEqualTo("1514764800000")
    }

    @Test
    fun `hasLifetimeAccess is converted`() {
        val hubSpotContact = HubSpotContactConverter().convert(
            CrmProfileFactory.sample(
                hasLifetimeAccess = true
            )
        )

        val hasLifetimeAccess = hubSpotContact.properties.first { it.property == "b2t_has_lifetime_access" }
        assertThat(hasLifetimeAccess.value).isEqualTo("true")
    }

    @Test
    fun `access expiry date is an instant`() {
        val hubSpotContact = HubSpotContactConverter().convert(
            CrmProfileFactory.sample(
                accessExpiresOn = Instant.parse("2018-01-01T00:00:00.123Z")
            )
        )

        val lastLoggedIn = hubSpotContact.properties.first { it.property == "b2t_access_expiry" }
        assertThat(lastLoggedIn.value).isEqualTo("1514764800000")
    }

    @Nested
    internal class UserRole {
        @Test
        fun `converting Teacher role`() = assertRoleConversion("TEACHER", "Teacher")

        @Test
        fun `converting Parent role`() = assertRoleConversion("PARENT", "Other")

        @Test
        fun `converting School Administrator role`() = assertRoleConversion("SCHOOLADMIN", "Administrator")

        @Test
        fun `converting Other role`() = assertRoleConversion("OTHER", "Other")

        @Test
        fun `empty role not sent`() {
            val hubSpotContact = HubSpotContactConverter().convert(
                CrmProfileFactory.sample(
                    role = ""
                )
            )

            val role = hubSpotContact.properties.firstOrNull { it.property == "b2t_role" }
            assertThat(role).isNull()
        }

        @Test
        fun `non-existing role not sent`() {
            val hubSpotContact = HubSpotContactConverter().convert(
                CrmProfileFactory.sample(
                    role = "DIRECTOR"
                )
            )

            val role = hubSpotContact.properties.firstOrNull { it.property == "b2t_role" }
            assertThat(role).isNull()
        }

        private fun assertRoleConversion(originalRole: String, expectedConversion: String) {
            val hubSpotContact = HubSpotContactConverter().convert(
                CrmProfileFactory.sample(
                    role = originalRole
                )
            )

            val role = hubSpotContact.properties.first { it.property == "b2t_role" }
            assertThat(role.value).isEqualTo(expectedConversion)
        }
    }
}
