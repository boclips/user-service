package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.InvalidCreateContentPackageException
import com.boclips.users.api.request.CreateContentPackageRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class CreateContentPackageIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var createContentPackage: CreateContentPackage

    @Test
    fun `throws when trying to create a content package with non existent access rules`() {
        assertThrows<InvalidCreateContentPackageException> {
            createContentPackage(
                CreateContentPackageRequest(
                    name = "content-package-name",
                    accessRuleIds = listOf("not-found")
                )
            )
        }
    }
}
