package com.boclips.users.client

import com.boclips.users.client.implementation.ApiUserServiceClient
import com.boclips.users.client.implementation.FakeUserServiceClient
import com.boclips.users.client.model.Subject
import com.boclips.users.client.model.TeacherPlatformAttributes
import com.boclips.users.client.model.User
import com.boclips.users.client.model.accessrule.SelectedCollectionsAccessRule
import com.boclips.users.client.model.accessrule.SelectedVideosAccessRule
import com.boclips.users.client.testsupport.AbstractClientIntegrationTest
import com.boclips.users.client.testsupport.config.ContractTestSecurityConfig.Companion.testPassword
import com.boclips.users.client.testsupport.config.ContractTestSecurityConfig.Companion.testUser
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import com.boclips.users.client.model.OrganisationDetails
import com.boclips.users.client.model.Organisation

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
    inner class GetAccessRulesOfUser {
        @Test
        fun `returns an empty list when user is not eligible to any access rules`() {
            val organisation = insertTestOrganisation("test-organisation-id")
            val user = insertTestUser(organisation)

            assertThat(client.getAccessRules(user.id)).isEmpty()
        }

        @Test
        fun `returns a list of access rules user is eligible to`() {
            val organisation = insertTestOrganisation(
                "test-organisation-id",
                listOf(
                    AccessRuleFactory.sampleSelectedCollectionsAccessRule(
                        name = "First",
                        collectionIds = listOf(CollectionId("A"), CollectionId("B"))
                    ),
                    AccessRuleFactory.sampleSelectedVideosAccessRule(
                        name = "Second",
                        videoIds = listOf(VideoId("C"), VideoId("D"))
                    )
                )
            )
            val user = insertTestUser(organisation)

            val accessRules = client.getAccessRules(user.id)

            assertThat(accessRules)
                .extracting("name")
                .containsExactlyInAnyOrder("First", "Second")

            val selectedCollectionsAccessRules = accessRules.filterIsInstance<SelectedCollectionsAccessRule>()
            assertThat(selectedCollectionsAccessRules)
                .flatExtracting("collectionIds")
                .containsExactlyInAnyOrder("A", "B")

            val selectedVideosAccessRules = accessRules.filterIsInstance<SelectedVideosAccessRule>()
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
        accessRules: List<AccessRule> = emptyList(),
        allowsOverridingUserIds: Boolean = false
    ): String

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
        accessRules: List<AccessRule>,
        allowsOverridingUserIds: Boolean
    ): String {
        return saveOrganisationWithAccessRuleDetails(name, accessRules.toList(), allowsOverridingUserIds).id.value
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
        accessRules: List<AccessRule>,
        allowsOverridingUserIds: Boolean
    ): String {
        accessRules.map { domainAccessRule ->
            when (domainAccessRule) {
                is AccessRule.SelectedCollections -> SelectedCollectionsAccessRule().apply {
                    this.name = domainAccessRule.name
                    this.collectionIds = domainAccessRule.collectionIds.map { it.value }
                }
                is AccessRule.SelectedVideos -> SelectedVideosAccessRule().apply {
                    this.name = domainAccessRule.name
                    this.videoIds = domainAccessRule.videoIds.map { it.value }
                }
            }
        }.forEach { convertedContract ->
            (client as FakeUserServiceClient).addAccessRule(convertedContract)
        }

        val organisationDetails = OrganisationDetails(allowsOverridingUserIds)
        val organisation = Organisation(name, organisationDetails)

        (client as FakeUserServiceClient).addOrganisation(organisation)

        return organisation.id
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
