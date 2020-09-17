package  com.boclips.users

import com.boclips.users.api.httpclient.ContentPackagesClient
import com.boclips.users.api.httpclient.OrganisationsClient
import com.boclips.users.api.httpclient.UsersClient
import com.boclips.users.api.httpclient.helper.ObjectMapperDefinition
import com.boclips.users.api.httpclient.helper.TestTokenFactory
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.user.Profile
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import feign.FeignException
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserServiceClientE2ETest : AbstractSpringIntegrationTest() {

    @Nested
    inner class Users {
        lateinit var usersClient: UsersClient

        @BeforeEach
        fun setUp() {
            usersClient = UsersClient.create(
                apiUrl = "http://localhost:$randomServerPort",
                objectMapper = ObjectMapperDefinition.default(),
                tokenFactory = TestTokenFactory(
                    "the@owner.com",
                    UserRoles.VIEW_USERS,
                    UserRoles.VIEW_ACCESS_RULES
                )
            )
        }

        @Test
        fun `can fetch a user`() {
            val user = saveUser(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = "123"),
                    profile = Profile(
                        firstName = "Mona",
                        lastName = "The Vampire"
                    )
                )
            )

            val fetchedUser = usersClient.getUser(user.id.value)
            assertThat(fetchedUser.id).isEqualTo(user.id.value)
            assertThat(fetchedUser.firstName).isEqualTo("Mona")
            assertThat(fetchedUser.lastName).isEqualTo("The Vampire")
        }

        @Test
        fun `can fetch access rules of a user`() {
            val accessRule = AccessRuleFactory.sampleIncludedVideosAccessRule(name = "Rule", videoIds =  emptyList())
            val contentPackage = saveContentPackage(
                ContentPackageFactory.sample(
                    accessRules = listOf(accessRule)
                )
            )
            val organisation = saveOrganisation(
                OrganisationFactory.apiIntegration(
                    deal = OrganisationFactory.deal(
                        contentPackageId = contentPackage.id
                    )
                )
            )
            val user = saveUser(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = "123"),
                    profile = Profile(
                        firstName = "Mona",
                        lastName = "The Vampire"
                    ),
                    organisation = organisation
                )
            )

            val fetchedAccessRules = usersClient.getAccessRulesOfUser(user.id.value)._embedded.accessRules
            assertThat(fetchedAccessRules).hasSize(1)
            assertThat(fetchedAccessRules.first())
        }

        @Test
        fun `throws when doesn't have access to share code`() {
            val user = saveUser(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = "123"),
                    profile = Profile(
                        firstName = "Mona",
                        lastName = "The Vampire"
                    ),
                    teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = "123")
                )
            )

            assertThrows<FeignException> { usersClient.getShareCode(user.id.value, "invalid") }
        }
    }

    @Nested
    inner class Organisations {
        lateinit var organisationsClient: OrganisationsClient

        @BeforeEach
        fun setUp() {
            organisationsClient = OrganisationsClient.create(
                apiUrl = "http://localhost:$randomServerPort",
                objectMapper = ObjectMapperDefinition.default(),
                tokenFactory = TestTokenFactory(
                    "the@owner.com",
                    UserRoles.VIEW_ORGANISATIONS
                )
            )
        }

        @Test
        fun `can fetch an organisation`() {
            val organisation = saveOrganisation(OrganisationFactory.apiIntegration(name = "hello"))
            val organisationResource = organisationsClient.getOrganisation(organisation.id.value)

            assertThat(organisationResource.id).isEqualTo(organisation.id.value)
            assertThat(organisationResource.organisationDetails.name).isEqualTo("hello")
        }
    }

    @Nested
    inner class ContentPackages {
        lateinit var contentPackagesClient: ContentPackagesClient

        @BeforeEach
        fun setUp() {
            contentPackagesClient = ContentPackagesClient.create(
                apiUrl = "http://localhost:$randomServerPort",
                objectMapper = ObjectMapperDefinition.default(),
                tokenFactory = TestTokenFactory(
                    "the@owner.com",
                    UserRoles.VIEW_CONTENT_PACKAGES
                )
            )
        }

        @Test
        fun `can fetch a content package`() {
            val contentPackage = saveContentPackage(
                ContentPackageFactory.sample(
                    name = "content package",
                    accessRules = listOf(
                        AccessRuleFactory.sampleIncludedVideosAccessRule()
                    )
                )
            )
            val resource = contentPackagesClient.find(contentPackage.id.value)

            assertThat(resource.id).isEqualTo(contentPackage.id.value)
            assertThat(resource.name).isEqualTo("content package")
            assertThat(resource.accessRules).hasSize(1)
        }
    }
}
