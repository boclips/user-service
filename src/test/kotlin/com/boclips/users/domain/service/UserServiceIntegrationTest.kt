package com.boclips.users.domain.service

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZoneOffset
import java.time.ZonedDateTime

class UserServiceIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `can find all teachers`() {
        val organisation =
            accountRepository.save(OrganisationDetailsFactory.school())
        val apiOrganisation =
            saveApiIntegration(organisation = OrganisationDetailsFactory.apiIntegration())

        listOf(
            saveUser(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = "1"),
                    organisationAccountId = organisation.id
                )
            ),
            saveUser(UserFactory.sample(identity = IdentityFactory.sample(id = "4"), organisationAccountId = null)),
            saveUser(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = "5"),
                    organisationAccountId = apiOrganisation.id
                )
            )
        )

        val users = userService.findAllTeachers()

        assertThat(users).hasSize(2)
        assertThat(users.map { it.id.value }).containsExactly("1", "4")
    }

    @Test
    fun `throws exception if user not found`() {
        val userId = UserId(value = "1234")

        assertThrows<UserNotFoundException> { userService.findUserById(userId) }
    }

    @Test
    fun `create teacher`() {
        val newUser = NewTeacher(
            email = "joe@dough.com",
            password = "thisisapassword",
            analyticsId = AnalyticsId(value = "analytics"),
            referralCode = "abc-a123",
            shareCode = "test",
            utmCampaign = "",
            utmSource = "",
            utmContent = "",
            utmTerm = "",
            utmMedium = ""
        )

        val timeBeforeCommand = ZonedDateTime.now(ZoneOffset.UTC)

        val persistedUser = userService.createTeacher(newUser)

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
        val newUser = NewTeacher(
            email = "joe@dough.com",
            password = "thisisapassword",
            analyticsId = AnalyticsId(value = "analytics"),
            referralCode = "abc-a123",
            shareCode = "test",
            utmCampaign = "",
            utmSource = "",
            utmContent = "",
            utmTerm = "",
            utmMedium = ""
        )

        val persistedUser = userService.createTeacher(newUser)

        assertThat(persistedUser.organisationAccountId).isNull()
    }
}
