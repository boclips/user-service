package com.boclips.users.domain.service.user

import com.boclips.users.domain.model.user.NewTeacher
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationTag
import com.boclips.users.domain.model.organisation.OrganisationTag.DEFAULT_ORGANISATION
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.district
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
    fun `individual teacher users get associated to the default organisation`() {
        saveOrganisation(district(name = "Boclips Free Trial", tags = setOf(DEFAULT_ORGANISATION)))

        val persistedUser = userCreationService.createTeacher(newUser)

        assertThat(persistedUser.organisation).isNotNull
        assertThat(persistedUser.organisation?.name).isEqualTo("Boclips Free Trial")
    }
}