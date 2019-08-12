package com.boclips.users.infrastructure.hubspot

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.testsupport.factories.CrmProfileFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class HubSpotContactConverterTest {

    @Test
    fun `maps all properties`() {
    }

    @Test
    fun `subjects are stored as comma separated string`() {
        val hubSpotContact =
            HubSpotContactConverter().convert(
                CrmProfileFactory.sample(
                    subjects = listOf(
                        Subject(id = SubjectId(value = "1"), name = "Maths"),
                        Subject(id = SubjectId(value = "2"), name = "Biology")
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
                pointInTime = Instant.parse("2018-01-01T00:00:00.123Z")
            )
        )

        val lastLoggedIn = hubSpotContact.properties.first { it.property == "b2t_last_logged_in" }
        assertThat(lastLoggedIn.value).isEqualTo("1514764800000")
    }
}