package com.boclips.users.domain.service.access

import com.boclips.security.utils.Client
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.model.organisation.ContentAccess
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.apiIntegration
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
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
                AccessRuleFactory.sampleIncludedVideosAccessRule(
                    name = "Included video access rule",
                    videoIds = emptyList()
                )
            )

            saveContentPackage(
                ContentPackageFactory.sample(
                    name = AccessRuleService.DEFAULT_CONTENT_PACKAGE_NAME,
                    accessRules = defaultAccessRules.map { it })
            )
        }

        @Test
        fun `can look up access rules for an existing organisation (necessary for api integrations)`() {
            val accessRule = AccessRuleFactory.sampleIncludedVideosAccessRule(name = "great rule", videoIds = listOf())
            val contentPackage = saveContentPackage(ContentPackageFactory.sample(accessRules = listOf(accessRule)))
            val organisation = saveOrganisation(
                apiIntegration(
                    deal = deal(
                        contentAccess = ContentAccess.SimpleAccess(contentPackage.id)
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
            val organisation = saveOrganisation(apiIntegration())
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

    @Nested
    inner class ClientBasedAccessRulesDefined {

        val organisationId = OrganisationId(ObjectId.get().toHexString())

        @BeforeEach
        fun `set up default content package and client based organisation`() {
            saveContentPackage(
                ContentPackage(
                    name = AccessRuleService.DEFAULT_CONTENT_PACKAGE_NAME,
                    id = ContentPackageId(ObjectId.get().toHexString()),
                    accessRules = listOf(
                        AccessRuleFactory.sampleExcludedVideoTypesAccessRule(
                            videoTypes = listOf(VideoType.NEWS),
                            name = "default access rule"
                        )
                    )
                )
            )

            val teachersContentPackage = saveContentPackage(
                ContentPackage(
                    name = "teachers content package",
                    id = ContentPackageId(ObjectId.get().toHexString()),
                    accessRules = listOf(AccessRuleFactory.sampleIncludedCollectionsAccessRule(name = "teachers rules"))
                )
            )
            val hqContentPackage = saveContentPackage(
                ContentPackage(
                    name = "hq content package",
                    id = ContentPackageId(ObjectId.get().toHexString()),
                    accessRules = listOf(AccessRuleFactory.sampleIncludedVideosAccessRule(name = "hq rules"))
                )
            )

            val clientBasedAccess = ContentAccess.ClientBasedAccess(
                mapOf(
                    Client.Teachers to teachersContentPackage.id,
                    Client.Hq to hqContentPackage.id,
                )
            )

            saveOrganisation(apiIntegration(id = organisationId, deal = deal(contentAccess = clientBasedAccess)))
        }

        @Test
        fun `can resolve access rule by client`() {
            val organisation = organisationRepository.findOrganisationById(organisationId)
            val accessRules =
                accessRuleService.forOrganisation(organisation = organisation, Client.getNameByClient(Client.Teachers))

            assertThat(accessRules.size).isEqualTo(1)
            assertThat(accessRules[0].name).isEqualTo("teachers rules")
        }

        @Test
        fun `falls back to default access rules if no client is provided for an org which has client based access`() {
            val organisation = organisationRepository.findOrganisationById(organisationId)
            val accessRules = accessRuleService.forOrganisation(organisation = organisation)

            assertThat(accessRules.size).isEqualTo(1)
            assertThat(accessRules[0].name).isEqualTo("default access rule")
        }

        @Test
        fun `if the organisation has simpleAccess, always return that content package id`() {
            val contentPackage = saveContentPackage(
                ContentPackage(
                    name = "custom content package",
                    id = ContentPackageId(ObjectId.get().toHexString()),
                    accessRules = listOf(AccessRuleFactory.sampleIncludedVideosAccessRule(name = "custom rules"))
                )
            )
            val organisation =
                saveOrganisation(apiIntegration(
                    deal = deal(contentAccess = ContentAccess.SimpleAccess(contentPackage.id))
                ))

            val accessRules = accessRuleService.forOrganisation(organisation = organisation, Client.getNameByClient(Client.Teachers))

            assertThat(accessRules.size).isEqualTo(1)
            assertThat(accessRules[0].name).isEqualTo("custom rules")
        }
    }
}
