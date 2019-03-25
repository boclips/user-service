package com.boclips.users.domain.service

import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var userService: UserService

    @Test
    fun `can find all users`() {
        val savedUsers = listOf(
            saveUser(
                UserFactory.sample(account = AccountFactory.sample(id = "1"))
            ),
            saveUser(
                UserFactory.sample(account = AccountFactory.sample(id = "2"))
            ),
            saveUser(
                UserFactory.sample(account = AccountFactory.sample(id = "3"))
            ),
            saveUser(
                UserFactory.sample(account = AccountFactory.sample(id = "4"))
            ),
            saveUser(
                UserFactory.sample(account = AccountFactory.sample(id = "5"))
            )
        )

        val users = userService.findAllUsers()

        assertThat(users.size).isEqualTo(savedUsers.size)
        assertThat(users.map { it.account.id.value }).contains("1", "2", "3", "4", "5")
    }

    @Test
    fun `create user`() {
        val newUser = NewUser(
            firstName = "Joe",
            lastName = "Dough",
            email = "joe@dough.com",
            password = "thisisapassword",
            subjects = "subject",
            analyticsId = AnalyticsId(value = "analytics"),
            referralCode = "abc-a123"
        )

        val persistedUser = userService.createUser(newUser)

        assertThat(persistedUser.account.firstName).isEqualTo("Joe")
        assertThat(persistedUser.account.lastName).isEqualTo("Dough")
        assertThat(persistedUser.account.email).isEqualTo("joe@dough.com")
        assertThat(persistedUser.account.subjects).isEqualTo("subject")
        assertThat(persistedUser.account.analyticsId).isEqualTo(AnalyticsId(value = "analytics"))
        assertThat(persistedUser.account.referralCode).isEqualTo("abc-a123")
    }
}