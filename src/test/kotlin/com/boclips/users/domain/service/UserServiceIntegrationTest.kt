package com.boclips.users.domain.service

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
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
            organisationAccountRepository.save(OrganisationFactory.school())
        val apiOrganisation =
            saveApiIntegration(organisation = OrganisationFactory.apiIntegration())

        listOf(
            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(id = "1"),
                    organisationAccountId = organisation.id
                )
            ),
            saveUser(UserFactory.sample(account = AccountFactory.sample(id = "4"), organisationAccountId = null)),
            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(id = "5"),
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
            utmCampaign = "",
            utmSource = "",
            utmContent = "",
            utmTerm = "",
            utmMedium = ""
        )

        val timeBeforeCommand = ZonedDateTime.now(ZoneOffset.UTC)

        val persistedUser = userService.createTeacher(newUser)

        assertThat(persistedUser.account.createdAt).isNotNull()
        assertThat(persistedUser.account.createdAt).isAfterOrEqualTo(timeBeforeCommand)
        assertThat(persistedUser.account.createdAt).isBeforeOrEqualTo(ZonedDateTime.now(ZoneOffset.UTC))
        assertThat(persistedUser.account.username).isEqualTo("joe@dough.com")
        assertThat(persistedUser.account.email).isEqualTo("joe@dough.com")
        assertThat(persistedUser.analyticsId).isEqualTo(AnalyticsId(value = "analytics"))
        assertThat(persistedUser.referralCode).isEqualTo("abc-a123")
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
