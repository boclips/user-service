package com.boclips.users.application.commands

import com.boclips.users.api.request.user.CreateE2EUserRequest
import com.boclips.users.application.exceptions.NotFoundException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class CreateE2EUserIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var createE2EUser: CreateE2EUser

    @Test
    fun `throws not found exception for invalid organisationId`() {
        assertThrows<NotFoundException> {
            createE2EUser(
                CreateE2EUserRequest(
                    email = "blah",
                    password = "blah",
                    organisationId = ObjectId.get().toHexString()
                )
            )
        }
    }

    @Test
    fun `throws not found exception for non hex organisation id`() {
        assertThrows<NotFoundException> {
            createE2EUser(
                CreateE2EUserRequest(
                    email = "blah",
                    password = "blah",
                    organisationId = "MISSING"
                )
            )
        }
    }
}
