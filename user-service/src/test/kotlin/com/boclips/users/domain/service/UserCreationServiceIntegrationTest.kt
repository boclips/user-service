package com.boclips.users.domain.service

import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class UserCreationServiceIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `create a user from identity`() {
        val organisation = organisationRepository.save(
            OrganisationFactory.apiIntegration(role = "ROLE_CLIENT_ORG")
        )

        val identity = IdentityFactory.sample(roles = listOf("ROLE_CLIENT_ORG"))
        val user: User = userCreationService.create(identity)

        assertThat(user.id).isEqualTo(identity.id)
        assertThat(user.identity.id).isEqualTo(identity.id)
        assertThat(user.identity.email).isEqualTo(identity.email)
        assertThat(user.identity.username).isEqualTo(identity.username)
        assertThat(user.identity.createdAt).isEqualTo(identity.createdAt)
        assertThat(user.organisation).isEqualTo(organisation)
        assertThat(user.profile?.hasOptedIntoMarketing).isEqualTo(false)
        assertThat(user.teacherPlatformAttributes?.hasLifetimeAccess).isEqualTo(false)
        assertThat(user.accessExpiresOn).isNull()
        assertThat(userRepository.findById(identity.id)).isEqualTo(user)
    }

    private val newUser = NewTeacher(
        email = "joe@dough.com",
        password = "thisisapassword",
        analyticsId = AnalyticsId(value = "analytics"),
        referralCode = "abc-a123",
        shareCode = "test",
        marketingTracking = MarketingTracking(
            utmCampaign = "",
            utmSource = "",
            utmContent = "",
            utmTerm = "",
            utmMedium = ""
        )
    )

    @Test
    fun `create teacher`() {
        val timeBeforeCommand = ZonedDateTime.now(ZoneOffset.UTC)

        val persistedUser = userCreationService.createTeacher(newUser)

        assertThat(persistedUser.identity.createdAt).isNotNull()
        assertThat(persistedUser.identity.createdAt).isAfterOrEqualTo(timeBeforeCommand)
        assertThat(persistedUser.identity.createdAt).isBeforeOrEqualTo(ZonedDateTime.now(ZoneOffset.UTC))
        assertThat(persistedUser.identity.username).isEqualTo("joe@dough.com")
        assertThat(persistedUser.identity.email).isEqualTo("joe@dough.com")
        assertThat(persistedUser.analyticsId).isEqualTo(AnalyticsId(value = "analytics"))
        assertThat(persistedUser.referralCode).isEqualTo("abc-a123")
        assertThat(persistedUser.teacherPlatformAttributes!!.shareCode).isEqualTo("test")
        assertThat(persistedUser.teacherPlatformAttributes!!.hasLifetimeAccess).isEqualTo(false)
        assertThat(persistedUser.marketingTracking.utmSource).isBlank()
        assertThat(persistedUser.marketingTracking.utmCampaign).isBlank()
        assertThat(persistedUser.marketingTracking.utmContent).isBlank()
        assertThat(persistedUser.marketingTracking.utmTerm).isBlank()
        assertThat(persistedUser.marketingTracking.utmMedium).isBlank()
    }

    @Test
    fun `an individual teacher user is not associated to external organisation`() {
        val persistedUser = userCreationService.createTeacher(newUser)

        assertThat(persistedUser.organisation).isNull()
    }
}
