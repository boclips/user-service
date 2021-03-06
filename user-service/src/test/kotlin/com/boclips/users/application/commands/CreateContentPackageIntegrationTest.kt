package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateContentPackageRequest
import com.boclips.users.application.exceptions.DuplicateContentPackageException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class CreateContentPackageIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var createContentPackage: CreateContentPackage

    @Test
    fun `throws when trying to create a duplicate content package`() {
        saveContentPackage(ContentPackageFactory.sample(name = "hello"))
        assertThrows<DuplicateContentPackageException> {
            createContentPackage(
                CreateContentPackageRequest(
                    name = "hello",
                    accessRules = emptyList()
                )
            )
        }
    }
}
