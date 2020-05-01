package com.boclips.users.domain.service

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.service.access.AccessRuleService
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.apiIntegration
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AccessRuleServiceTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var accessRuleService: AccessRuleService

    @Nested
    inner class WithDefaultContentPackageDefined {
        lateinit var defaultAccessRules: List<AccessRule>

        @BeforeEach
        fun `set up default content package`() {
            defaultAccessRules = listOf(
                saveIncludedVideosAccessRule(name = "Included video access rule", videoIds = emptyList())
            )

            saveContentPackage(
                ContentPackageFactory.sample(
                    name = AccessRuleService.DEFAULT_CONTENT_PACKAGE_NAME,
                    accessRuleIds = defaultAccessRules.map { it.id })
            )
        }

        @Test
        fun `can look up access rules for an existing organisation (necessary for api integrations)`() {
            val accessRule = saveIncludedVideosAccessRule(name = "great rule", videoIds = listOf())
            val contentPackage = saveContentPackage(ContentPackageFactory.sample(accessRuleIds = listOf(accessRule.id)))
            val organisation = saveOrganisation(
                apiIntegration(
                    deal = deal(
                        contentPackageId = contentPackage.id
                    )
                )
            )

            val accessRules = accessRuleService.forOrganisation(organisation = organisation)

            assertThat(accessRules).containsExactly(accessRule)
        }

        @Test
        fun `defaults for users without an organisation associated to (necessary for early teacher users)`() {
            val accessRules = accessRuleService.forOrganisation(organisation = null)

            assertThat(accessRules).containsExactlyInAnyOrder(*defaultAccessRules.toTypedArray())
        }

        @Test
        fun `defaults for organisations without a content package (necessary for schools and districts)`() {
            val organisation = saveOrganisation(
                apiIntegration(
                    deal = deal(
                        contentPackageId = null
                    )
                )
            )
            val accessRules = accessRuleService.forOrganisation(organisation)

            assertThat(accessRules).containsExactlyInAnyOrder(*defaultAccessRules.toTypedArray())
        }
    }

    @Nested
    inner class WithoutDefaultPackageDefined {
        @Test
        fun `returns empty access rules when no content package fallback found (this should not happen)`() {
            val accessRules = accessRuleService.forOrganisation(organisation = null)

            assertThat(accessRules).isEmpty()
        }
    }
}
