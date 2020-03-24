package com.boclips.users.application.commands

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GetAccessRulesOfUserTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var getAccessRulesOfUser: GetAccessRulesOfUser

    @Test
    fun `can get the access rules of a user`() {
        val videoAccessRule = saveIncludedVideosAccessRule(name = "Video access rule", videoIds = emptyList())
        val contentPackage = ContentPackageFactory.sample(accessRuleIds = listOf(videoAccessRule.id))

        saveContentPackage(contentPackage)

        val organisation = saveApiIntegration(contentPackageId = contentPackage.id)
        val user = saveUser(UserFactory.sample(organisationId = organisation.id))

        val accessRulesOfUser = getAccessRulesOfUser(user.id.value)
        assertThat(accessRulesOfUser).containsExactly(videoAccessRule)
    }
}