package com.boclips.users.domain.service

import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UpdatedUser
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TeachersPlatformServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var teachersPlatformService: TeachersPlatformService

    @Test
    fun `can find all users`() {
        val savedUsers = listOf(
            saveUser(UserFactory.sample(id = "1")),
            saveUser(UserFactory.sample(id = "2")),
            saveUser(UserFactory.sample(id = "3")),
            saveUser(UserFactory.sample(id = "4")),
            saveUser(UserFactory.sample(id = "5"))
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
            subjects = listOf(Subject(id = SubjectId("test"), name = "subject")),
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

        val organisation = saveOrganisation("Boclips for Teachers")

        val persistedUser = teachersPlatformService.createUser(newUser)

        assertThat(persistedUser.firstName).isEqualTo("Joe")
        assertThat(persistedUser.lastName).isEqualTo("Dough")
        assertThat(persistedUser.email).isEqualTo("joe@dough.com")
        assertThat(persistedUser.subjects).hasSize(1)
        assertThat(persistedUser.ages).containsExactly(1, 2)
        assertThat(persistedUser.analyticsId).isEqualTo(AnalyticsId(value = "analytics"))
        assertThat(persistedUser.referralCode).isEqualTo("abc-a123")
        assertThat(persistedUser.hasOptedIntoMarketing).isTrue()
        assertThat(persistedUser.marketingTracking.utmSource).isBlank()
        assertThat(persistedUser.marketingTracking.utmCampaign).isBlank()
        assertThat(persistedUser.marketingTracking.utmContent).isBlank()
        assertThat(persistedUser.marketingTracking.utmTerm).isBlank()
        assertThat(persistedUser.marketingTracking.utmMedium).isBlank()
        assertThat(persistedUser.associatedTo).isEqualTo(organisation.id)
    }

    @Test
    fun `update user details`() {
        saveUser(UserFactory.sample(id = "user-id"))

        val newUserDetails = UpdatedUser(
            userId = UserId("user-id"),
            firstName = "Joe",
            lastName = "Dough",
            subjects = listOf(Subject(id = SubjectId("test"), name = "subject")),
            ages = listOf(1, 2),
            hasOptedIntoMarketing = true
        )

        val persistedUser = teachersPlatformService.updateUserDetails(newUserDetails)

        assertThat(persistedUser.firstName).isEqualTo("Joe")
        assertThat(persistedUser.lastName).isEqualTo("Dough")
        assertThat(persistedUser.email).isEqualTo("joe@dough.com")
        assertThat(persistedUser.subjects).hasSize(1)
        assertThat(persistedUser.ages).containsExactly(1, 2)
        assertThat(persistedUser.hasOptedIntoMarketing).isTrue()
    }
}
