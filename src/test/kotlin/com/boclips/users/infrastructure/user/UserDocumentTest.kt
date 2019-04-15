package com.boclips.users.infrastructure.user

import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.UserDocumentFactory
import com.boclips.users.testsupport.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserDocumentTest {
    @Test
    fun `can convert document to user`() {
        val user = UserFactory.sample(user = AccountFactory.sample(hasOptedIntoMarketing = true))

        val convertedUser = UserDocument.from(user).toUser()

        assertThat(convertedUser).isEqualTo(user)
    }

    @Test
    fun `users missing optedIntoMarketing is defaulted to true`() {
        val convertedUser = UserDocumentFactory.sample(hasOptedIntoMarketing = null).toUser()

        assertThat(convertedUser.hasOptedIntoMarketing).isTrue()
    }
}