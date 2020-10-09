package com.boclips.users.application.commands

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.apiIntegration
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GetAccessRulesOfUserTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var getAccessRulesOfUser: GetAccessRulesOfUser

    @Test
    fun `can get the access rules of a user`() {
        val videoAccessRule =
            AccessRuleFactory.sampleIncludedVideosAccessRule(name = "Video access rule", videoIds = emptyList())
        val contentPackage = ContentPackageFactory.sample(accessRules = listOf(videoAccessRule))

        saveContentPackage(contentPackage)

        val organisation = saveOrganisation(
            apiIntegration(deal = deal(contentPackageId = contentPackage.id))
        )
        val user = saveUser(UserFactory.sample(organisation = organisation))

        val accessRulesOfUser = getAccessRulesOfUser(user.id.value)
        assertThat(accessRulesOfUser).containsExactly(videoAccessRule)
    }
}
