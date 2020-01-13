package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ShareCodeNotFoundException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.User
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.MarketingTrackingFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class ValidateShareCodeTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var validateShareCode: ValidateShareCode

    @Test
    fun `valid share code`() {
        val user = saveUser(UserFactory.sample(teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = "CODE")))
        assertTrue(validateShareCode(user.id.value, "CODE"))
    }
    @Test
    fun `valid share code - case insensitive`() {
        val user = saveUser(UserFactory.sample(teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = "CASE")))
        assertTrue(validateShareCode(user.id.value, "cAsE"))
    }

    @Test
    fun `invalid share code`() {
        val user = saveUser(UserFactory.sample(teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = "CODE")))
        assertFalse(validateShareCode(user.id.value, "BADCODE"))
    }

    @Test
    fun `user doesn't exist`() {
        assertThrows<UserNotFoundException>{validateShareCode("non-existent-user", "CODE")}
    }

    @Test
    fun `user does not have teacher platform attributes`() {
        val user = saveUser(
            User(
                identity = IdentityFactory.sample(),
                profile = ProfileFactory.sample(),
                analyticsId = null,
                referralCode = null,
                teacherPlatformAttributes = null,
                marketingTracking = MarketingTrackingFactory.sample(),
                organisationAccountId = null,
                accessExpiresOn = null)
        )
        assertThrows<ShareCodeNotFoundException>{validateShareCode(user.id.value, "CODE")}
    }

    @Test
    fun `user does not have a share code`() {
        val user = saveUser(UserFactory.sample(teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = null)))
        assertThrows<ShareCodeNotFoundException>{validateShareCode(user.id.value, "CODE")}
    }
}
