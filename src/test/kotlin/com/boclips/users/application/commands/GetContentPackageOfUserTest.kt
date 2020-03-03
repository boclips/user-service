package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ContentPackageNotForUserFoundException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class GetContentPackageOfUserTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var getContentPackageOfUser: GetContentPackageOfUser

    @Test
    fun `can get the content package of a user`() {
        val contentPackage = ContentPackageFactory.sampleContentPackage()

        saveContentPackage(contentPackage)

        val organisation = saveApiIntegration(contentPackageId = contentPackage.id)
        val user = saveUser(UserFactory.sample(organisationId = organisation.id))

        val retrievedContentPackageOfUser = getContentPackageOfUser(user.id.value)
        assertThat(contentPackage).isEqualTo(retrievedContentPackageOfUser)
    }

    @Test
    fun `throws when no content package for that user`() {
        assertThrows<ContentPackageNotForUserFoundException> {
            val organisation = saveApiIntegration(contentPackageId = null)
            val user = saveUser(UserFactory.sample(organisationId = organisation.id))

            getContentPackageOfUser(user.id.value)
        }
    }
}
