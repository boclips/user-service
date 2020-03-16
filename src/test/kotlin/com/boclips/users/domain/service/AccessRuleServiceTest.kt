package com.boclips.users.domain.service

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AccessRuleServiceTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var accessRuleService: AccessRuleService

    @Test
    fun `can look up access rules for an existing organisation`() {
        val accessRule = saveIncludedVideosAccessRule(name = "great rule", videoIds = listOf())
        val contentPackage = saveContentPackage(ContentPackageFactory.sample(accessRuleIds = listOf(accessRule.id)))
        val organisation = saveOrganisationWithContentPackage(contentPackageId = contentPackage.id)

        val accessRules = accessRuleService.forOrganisation(organisation = organisation)

        assertThat(accessRules).containsExactly(accessRule)
    }

    @Test
    fun `can look up access rules for users without an organisation associated to`() {
        val accessRules = accessRuleService.forOrganisation(organisation = null)

        assertThat(accessRules).isEmpty()
    }
}
