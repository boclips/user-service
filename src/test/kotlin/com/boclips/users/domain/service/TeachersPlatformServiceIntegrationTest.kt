package com.boclips.users.domain.service

import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TeachersPlatformServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var teachersPlatformService: TeachersPlatformService

    @Test
    fun `can find all users`() {
        val savedUsers = listOf(
            saveUser(
                UserFactory.sample(user = AccountFactory.sample(id = "1"))
            ),
            saveUser(
                UserFactory.sample(user = AccountFactory.sample(id = "2"))
            ),
            saveUser(
                UserFactory.sample(user = AccountFactory.sample(id = "3"))
            ),
            saveUser(
                UserFactory.sample(user = AccountFactory.sample(id = "4"))
            ),
            saveUser(
                UserFactory.sample(user = AccountFactory.sample(id = "5"))
            )
        )

        val users = teachersPlatformService.findAllUsers()

        assertThat(users.size).isEqualTo(savedUsers.size)
        assertThat(users.map { it.id.value }).contains("1", "2", "3", "4", "5")
    }

    @Test
    fun `create user`() {
        val newUser = NewUser(
            firstName = "Joe",
            lastName = "Dough",
            email = "joe@dough.com",
            password = "thisisapassword",
            subjects = listOf("subject"),
            ageRange = listOf(1, 2),
            analyticsId = AnalyticsId(value = "analytics"),
            referralCode = "abc-a123",
            hasOptedIntoMarketing = true,
            utmCampaign = "",
            utmSource = "",
            utmContent = "",
            utmTerm = "",
            utmMedium = ""
        )

        val organisation = saveOrganisation("Boclips For Teachers")

        val persistedUser = teachersPlatformService.createUser(newUser)

        assertThat(persistedUser.firstName).isEqualTo("Joe")
        assertThat(persistedUser.lastName).isEqualTo("Dough")
        assertThat(persistedUser.email).isEqualTo("joe@dough.com")
        assertThat(persistedUser.subjects).hasSize(1)
        assertThat(persistedUser.ageRange).containsExactly(1, 2)
        assertThat(persistedUser.analyticsId).isEqualTo(AnalyticsId(value = "analytics"))
        assertThat(persistedUser.referralCode).isEqualTo("abc-a123")
        assertThat(persistedUser.hasOptedIntoMarketing).isTrue()
        assertThat(persistedUser.marketingTracking.utmSource).isBlank()
        assertThat(persistedUser.marketingTracking.utmCampaign).isBlank()
        assertThat(persistedUser.marketingTracking.utmContent).isBlank()
        assertThat(persistedUser.marketingTracking.utmTerm).isBlank()
        assertThat(persistedUser.marketingTracking.utmMedium).isBlank()
        assertThat(persistedUser.organisationId).isEqualTo(organisation.id)
    }
}
