package com.boclips.users.client

import com.boclips.users.client.implementation.ApiUserServiceClient
import com.boclips.users.client.implementation.FakeUserServiceClient
import com.boclips.users.client.model.Organisation
import com.boclips.users.client.model.OrganisationDetails
import com.boclips.users.client.model.Subject
import com.boclips.users.client.model.TeacherPlatformAttributes
import com.boclips.users.client.model.User
import com.boclips.users.client.model.accessrule.ContentPackage
import com.boclips.users.client.model.accessrule.SelectedCollectionsAccessRule
import com.boclips.users.client.model.accessrule.SelectedVideosAccessRule
import com.boclips.users.client.testsupport.AbstractClientIntegrationTest
import com.boclips.users.client.testsupport.config.ContractTestSecurityConfig.Companion.testPassword
import com.boclips.users.client.testsupport.config.ContractTestSecurityConfig.Companion.testUser
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder

abstract class UserServiceClientContractTest : AbstractClientIntegrationTest() {
    @Nested
    inner class FindUser {
        @Test
        fun `returns null if it doesn't find the user`() {
            assertThat(client.findUser("this does not exist")).isNull()
        }

        @Test
        fun `returns user corresponding to provided id`() {
            val organisation = insertTestOrganisation("test-organisation-id")
            val user = insertTestUser(organisation, subjectId = "subject-1", shareCode = "abcd")

            val retrievedUser = client.findUser(user.id)
            assertThat(retrievedUser.id).isEqualTo(user.id)
            assertThat(retrievedUser.organisationAccountId).isEqualTo(user.organisationAccountId)
            assertThat(retrievedUser.subjects).containsExactly(Subject("subject-1"))
            assertThat(retrievedUser.teacherPlatformAttributes.shareCode).isEqualTo(user.teacherPlatformAttributes.shareCode)
        }
    }

    @Nested
    inner class ValidateShareCode {
        @Test
        fun `returns true if share code is correct`() {
            val organisation = insertTestOrganisation("test-organisation-id")
            val user = insertTestUser(organisation, shareCode = "TEST")
            assertThat(client.validateShareCode(user.id, user.teacherPlatformAttributes.shareCode)).isEqualTo(true)
        }

        @Test
        fun `returns false if share code is incorrect`() {
            val organisation = insertTestOrganisation("test-organisation-id")
            val user = insertTestUser(organisation, shareCode = "TEST")
            assertThat(client.validateShareCode(user.id, "BAD")).isEqualTo(false)
        }

        @Test
        fun `returns false if share code is not provided`() {
            val organisation = insertTestOrganisation("test-organisation-id")
            val user = insertTestUser(organisation, shareCode = "TEST")
            assertThat(client.validateShareCode(user.id, null)).isEqualTo(false)
        }

        @Test
        fun `returns false if user id is not provided`() {
            val organisation = insertTestOrganisation("test-organisation-id")
            insertTestUser(organisation, shareCode = "TEST")
            assertThat(client.validateShareCode(null, "TEST")).isEqualTo(false)
        }
    }

    @Nested
    inner class GetContentPackageOfUser {
        @Test
        fun `returns a package of an empty list of access rules when user has no permitted access rules`() {
            val organisation = insertTestOrganisation("test-organisation-id", null)
            val user = insertTestUser(organisation)

            assertThat(client.getContentPackage(user.id)).isNull()
        }

        @Test
        fun `returns a package of a user's permitted access rules`() {
            val accessRuleCollections = saveSelectedCollectionsAccessRule(
                name = "First",
                collectionIds = listOf(CollectionId("A"), CollectionId("B"))
            )

            val accessRuleVideos = saveSelectedVideosAccessRule(
                name = "Second",
                videoIds = listOf(VideoId("C"), VideoId("D"))
            )
            val contentPackageId =
                insertContentPackage("My content package", listOf(accessRuleCollections, accessRuleVideos))

            val organisation = insertTestOrganisation(
                "test-organisation-id", contentPackageId

            )
            val user = insertTestUser(organisation)

            val contentPackage = client.getContentPackage(user.id)

            assertThat(contentPackage)
                .extracting("name")
                .contains("My content package")

            val selectedCollectionsAccessRules =
                contentPackage.accessRules.filterIsInstance<SelectedCollectionsAccessRule>()
            assertThat(selectedCollectionsAccessRules)
                .flatExtracting("collectionIds")
                .containsExactlyInAnyOrder("A", "B")

            val selectedVideosAccessRules = contentPackage.accessRules.filterIsInstance<SelectedVideosAccessRule>()
            assertThat(selectedVideosAccessRules)
                .flatExtracting("videoIds")
                .containsExactlyInAnyOrder("C", "D")
        }
    }

    @Nested
    inner class GetOrganisation {
        @Test
        fun `returns an organisation`() {
            val organisationId = insertTestOrganisation(
                name = "overriding org",
                allowsOverridingUserIds = true
            )

            insertTestUser(organisationId)

            val organisation = client.getOrganisation(organisationId)

            assertThat(organisation.organisationDetails.allowsOverridingUserIds).isTrue()
        }
    }

    abstract fun insertTestOrganisation(
        name: String,
        contentPackageId: ContentPackageId? = null,
        allowsOverridingUserIds: Boolean = false
    ): String

    abstract fun insertContentPackage(name: String, accessRules: List<AccessRule>): ContentPackageId

    abstract fun insertTestUser(
        organisationId: String,
        subjectId: String = "1",
        shareCode: String = "test"
    ): User

    lateinit var client: UserServiceClient
}

class ApiUserServiceClientContractTest : UserServiceClientContractTest() {
    @BeforeEach
    fun initialiseApiClient() {
        client = ApiUserServiceClient(
            userServiceUrl(),
            RestTemplateBuilder()
                .basicAuthentication(testUser, testPassword)
                .build()
        )
    }

    override fun insertTestOrganisation(
        name: String,
        contentPackageId: ContentPackageId?,
        allowsOverridingUserIds: Boolean
    ): String {

        return saveOrganisationWithContentPackage(
            organisationName = name,
            contentPackageId = contentPackageId,
            allowsOverridingUserIds = allowsOverridingUserIds
        ).id.value
    }

    override fun insertContentPackage(
        name: String,
        accessRules: List<AccessRule>
    ): ContentPackageId {
        return saveContentPackage(
            ContentPackageFactory.sampleContentPackage(
                name = name,
                accessRuleIds = accessRules.map { it.id })
        ).id
    }

    override fun insertTestUser(organisationId: String, subjectId: String, shareCode: String): User {
        subjectService.addSubject(com.boclips.users.domain.model.Subject(id = SubjectId(subjectId), name = subjectId))

        val user = UserFactory.sample(
            organisationId = OrganisationId(organisationId),
            profile = ProfileFactory.sample(
                subjects = listOf(
                    com.boclips.users.domain.model.Subject(
                        id = SubjectId(
                            subjectId
                        ), name = ""
                    )
                )
            ),
            teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = shareCode)
        )

        saveUser(user)

        return User(
            user.id.value,
            user.organisationId!!.value,
            listOf(Subject(subjectId)),
            TeacherPlatformAttributes(shareCode)
        )
    }
}

class FakeUserServiceClientContractTest : UserServiceClientContractTest() {
    @BeforeEach
    fun initialiseFakeClient() {
        client = FakeUserServiceClient()
    }

    override fun insertTestOrganisation(
        name: String,
        contentPackageId: ContentPackageId?,
        allowsOverridingUserIds: Boolean
    ): String {
        val organisationDetails = OrganisationDetails(allowsOverridingUserIds)
        val organisation = Organisation(name, contentPackageId?.value, organisationDetails)

        (client as FakeUserServiceClient).addOrganisation(organisation)

        return organisation.id
    }

    override fun insertContentPackage(name: String, accessRules: List<AccessRule>): ContentPackageId {
        val id = ObjectId.get().toHexString()
        (client as FakeUserServiceClient).addContentPackage(
            ContentPackage.builder()
                .id(id)
                .name(name)
                .accessRules(accessRules.map {
                    when (it) {
                        is AccessRule.SelectedCollections -> SelectedCollectionsAccessRule(it.collectionIds.map { id -> id.value })
                        is AccessRule.IncludedCollections -> SelectedCollectionsAccessRule(it.collectionIds.map { id -> id.value })
                        is AccessRule.SelectedVideos -> SelectedVideosAccessRule(it.videoIds.map { id -> id.value })
                        is AccessRule.IncludedVideos -> SelectedVideosAccessRule(it.videoIds.map { id -> id.value })
                    }
                }).build()
        )

        return ContentPackageId(value = id)
    }

    override fun insertTestUser(organisationId: String, subjectId: String, shareCode: String): User {
        return (client as FakeUserServiceClient).addUser(
            User(
                "idontcare",
                "scam",
                listOf(Subject(subjectId)),
                TeacherPlatformAttributes(shareCode)
            )
        )
    }
}
