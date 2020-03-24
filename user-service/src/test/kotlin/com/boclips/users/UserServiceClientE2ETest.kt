package  com.boclips.users

import com.boclips.users.api.httpclient.OrganisationsClient
import com.boclips.users.api.httpclient.UsersClient
import com.boclips.users.api.httpclient.helper.ObjectMapperDefinition
import com.boclips.users.api.httpclient.helper.TestTokenFactory
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.Profile
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import feign.FeignException
import org.assertj.core.api.Assertions.assertThat
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
                    profile = Profile(firstName = "Mona", lastName = "The Vampire")
                )
            )

            val fetchedUser = usersClient.getUser(user.id.value)
            assertThat(fetchedUser.id).isEqualTo(user.id.value)
            assertThat(fetchedUser.firstName).isEqualTo("Mona")
            assertThat(fetchedUser.lastName).isEqualTo("The Vampire")
        }

        @Test
        fun `can fetch access rules of a user`() {
            val accessRule = saveIncludedVideosAccessRule("Rule", emptyList())
            val contentPackage = saveContentPackage(
                ContentPackageFactory.sample(
                    accessRuleIds = listOf(accessRule.id)
                )
            )
            val organisation = saveOrganisationWithContentPackage(contentPackageId = contentPackage.id)
            val user = saveUser(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = "123"),
                    profile = Profile(firstName = "Mona", lastName = "The Vampire"),
                    organisationId = organisation.id
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
                    profile = Profile(firstName = "Mona", lastName = "The Vampire"),
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
            val organisation = saveOrganisationWithContentPackage(organisationName = "hello")
            val organisationResource = organisationsClient.getOrganisation(organisation.id.value)

            assertThat(organisationResource.id).isEqualTo(organisation.id.value)
            assertThat(organisationResource.organisationDetails.name).isEqualTo("hello")
        }
    }
}
