package com.boclips.users.domain.service

import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UpdatedUser
import com.boclips.users.domain.model.UserId
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.UserFactory
import com.boclips.users.testsupport.factories.UserSourceFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class UserServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var userService: UserService

    @Test
    fun `can find all teachers`() {
        listOf(
            saveUser(UserFactory.sample(id = "1", associatedTo = UserSourceFactory.apiClientSample())),
            saveUser(UserFactory.sample(id = "2", associatedTo = UserSourceFactory.apiClientSample())),
            saveUser(UserFactory.sample(id = "3", associatedTo = UserSourceFactory.apiClientSample())),
            saveUser(UserFactory.sample(id = "4", associatedTo = UserSourceFactory.apiClientSample())),
            saveUser(UserFactory.sample(id = "5", associatedTo = UserSource.Boclips))
        )

        val users = userService.findAllTeachers()

        assertThat(users).hasSize(1)
        assertThat(users.map { it.id.value }).containsExactly("5")
    }

    @Test
    fun `fails to find teacher if user is not teacher`() {
        saveUser(UserFactory.sample(id = "1", associatedTo = UserSourceFactory.apiClientSample()))

        assertThrows<UserNotFoundException> { userService.findTeacherById(UserId("1")) }
    }

    @Test
    fun `create user`() {
        val newUser = NewUser(
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

        assertThat(persistedUser.email).isEqualTo("joe@dough.com")
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
        val newUser = NewUser(
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

        assertThat(persistedUser.associatedTo).isEqualTo(UserSource.Boclips)
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

        val persistedUser = userService.updateUserDetails(newUserDetails)

        assertThat(persistedUser.firstName).isEqualTo("Joe")
        assertThat(persistedUser.lastName).isEqualTo("Dough")
        assertThat(persistedUser.email).isEqualTo("joe@dough.com")
        assertThat(persistedUser.subjects).hasSize(1)
        assertThat(persistedUser.ages).containsExactly(1, 2)
        assertThat(persistedUser.hasOptedIntoMarketing).isTrue()
    }
}
