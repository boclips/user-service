package com.boclips.users.domain.service

import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UserId
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.Platform
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
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
            saveUser(UserFactory.sample(account = AccountFactory.sample(id = "1", platform = UserSourceFactory.apiClientSample()))),
            saveUser(UserFactory.sample(account = AccountFactory.sample(id = "2", platform = UserSourceFactory.apiClientSample()))),
            saveUser(UserFactory.sample(account = AccountFactory.sample(id = "3", platform = UserSourceFactory.apiClientSample()))),
            saveUser(UserFactory.sample(account = AccountFactory.sample(id = "4", platform = UserSourceFactory.apiClientSample()))),
            saveUser(UserFactory.sample(account = AccountFactory.sample(id = "5", platform = Platform.BoclipsForTeachers)))
        )

        val users = userService.findAllTeachers()

        assertThat(users).hasSize(1)
        assertThat(users.map { it.id.value }).containsExactly("5")
    }

    @Test
    fun `fails to find teacher if user is not teacher`() {
        saveUser(UserFactory.sample(account = AccountFactory.sample(id = "1", platform = UserSourceFactory.apiClientSample())))

        assertThrows<UserNotFoundException> { userService.findTeacherById(UserId("1")) }
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

        val persistedUser = userService.createTeacher(newUser)

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

        assertThat(persistedUser.account.platform).isEqualTo(Platform.BoclipsForTeachers)
    }

    @Test
    fun `update profile`() {
        saveUser(UserFactory.sample(id = "user-id"))

        val profile = Profile(
            firstName = "Joe",
            lastName = "Dough",
            subjects = listOf(Subject(id = SubjectId("test"), name = "subject")),
            ages = listOf(1, 2),
            hasOptedIntoMarketing = true
        )

        val persistedUser = userService.updateProfile(userId = UserId("user-id"), profile = profile)

        assertThat(persistedUser.profile!!.firstName).isEqualTo("Joe")
        assertThat(persistedUser.profile!!.lastName).isEqualTo("Dough")
        assertThat(persistedUser.profile!!.subjects).hasSize(1)
        assertThat(persistedUser.profile!!.ages).containsExactly(1, 2)
        assertThat(persistedUser.profile!!.hasOptedIntoMarketing).isTrue()
    }
}
