package com.boclips.users.domain.service

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.marketing.CrmProfile
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class ConvertUserToCrmProfileKtTest : AbstractSpringIntegrationTest() {

    @Test
    fun `user is activated if it has set profile information`() {
        val user = UserFactory.sample(profile = ProfileFactory.sample())

        val crmProfile: CrmProfile = convertUserToCrmProfile(user, UserSessions(Instant.now()))!!

        assertThat(crmProfile.activated).isTrue()
    }

    @Test
    fun `user is not activated if it has set profile information`() {
        val user = UserFactory.sample(profile = ProfileFactory.sample(firstName = ""))

        val crmProfile: CrmProfile = convertUserToCrmProfile(user, UserSessions(Instant.now()))!!

        assertThat(crmProfile.activated).isFalse()
    }

    @Test
    fun `it maps a User to a CRM Profile`() {
        val expiryDate = ZonedDateTime.now(ZoneOffset.UTC).plusMonths(1)
        val user = UserFactory.sample(
            identity = IdentityFactory.sample(id = "lovely-user-id", username = "lovely-user@boclips.com"),
            profile = ProfileFactory.sample(
                firstName = "First",
                lastName = "Last",
                ages = listOf(1, 2, 3, 4),
                subjects = listOf(Subject(id = SubjectId("subject-id"), name = "Subject Name")),
                role = "TEACHER",
                hasOptedIntoMarketing = true
            ),
            accessExpiresOn = expiryDate,
            marketing = MarketingTracking(
                utmSource = "test-source",
                utmMedium = "test-medium",
                utmCampaign = "test-campaign",
                utmTerm = "test-term",
                utmContent = "test-content"
            )
        )

        val crmProfile: CrmProfile = convertUserToCrmProfile(user, UserSessions(Instant.now()))!!

        assertThat(crmProfile.firstName).isEqualTo("First")
        assertThat(crmProfile.lastName).isEqualTo("Last")
        assertThat(crmProfile.email).isEqualTo("lovely-user@boclips.com")
        assertThat(crmProfile.activated).isTrue()
        assertThat(crmProfile.hasOptedIntoMarketing).isTrue()
        assertThat(crmProfile.ageRange).containsExactly(1, 2, 3, 4)
        assertThat(crmProfile.subjects).hasSize(1)
        assertThat(crmProfile.subjects.first().name).isEqualTo("Subject Name")
        assertThat(crmProfile.subjects.first().id).isEqualTo(SubjectId("subject-id"))
        assertThat(crmProfile.role).isEqualTo("TEACHER")
        assertThat(crmProfile.accessExpiresOn).isEqualTo(expiryDate.toInstant())
        assertThat(crmProfile.marketingTracking.utmSource).isEqualTo("test-source")
        assertThat(crmProfile.marketingTracking.utmMedium).isEqualTo("test-medium")
        assertThat(crmProfile.marketingTracking.utmCampaign).isEqualTo("test-campaign")
        assertThat(crmProfile.marketingTracking.utmTerm).isEqualTo("test-term")
        assertThat(crmProfile.marketingTracking.utmContent).isEqualTo("test-content")
    }
}
