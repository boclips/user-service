package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateAccessRuleRequest
import com.boclips.users.application.exceptions.InvalidVideoTypeException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class CreateAccessRuleTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var createAccessRule: CreateAccessRule

    @Test
    fun `throws when creating an access rule with the wrong video type`() {
        assertThrows<InvalidVideoTypeException> {
            createAccessRule.invoke(CreateAccessRuleRequest.ExcludedVideoTypes().apply {
                name = "an access rule"
                videoTypes = listOf("invalid", "news")
            })
        }
    }
}
