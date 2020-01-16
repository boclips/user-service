package com.boclips.users.client

import com.boclips.users.client.implementation.ApiUserServiceClient
import com.boclips.users.client.implementation.FakeUserServiceClient
import com.boclips.users.client.model.Subject
import com.boclips.users.client.model.TeacherPlatformAttributes
import com.boclips.users.client.model.User
import com.boclips.users.client.model.contract.SelectedCollectionsContract
import com.boclips.users.client.model.contract.SelectedVideosContract
import com.boclips.users.client.testsupport.AbstractClientIntegrationTest
import com.boclips.users.client.testsupport.config.ContractTestSecurityConfig.Companion.testPassword
import com.boclips.users.client.testsupport.config.ContractTestSecurityConfig.Companion.testUser
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.domain.model.contract.VideoId
import com.boclips.users.testsupport.factories.ContractFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import com.boclips.users.client.model.Account as ClientAccount
import com.boclips.users.client.model.Organisation as ClientOrganisation
import com.boclips.users.domain.model.contract.Contract as DomainContract

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
    inner class GetContractsOfUser {
        @Test
        fun `returns an empty list when user is not eligible to any contracts`() {
            val organisation = insertTestOrganisation("test-organisation-id")
            val user = insertTestUser(organisation)

            assertThat(client.getContracts(user.id)).isEmpty()
        }

        @Test
        fun `returns a list of contracts user is eligible to`() {
            val organisation = insertTestOrganisation(
                "test-organisation-id",
                listOf(
                    ContractFactory.sampleSelectedCollectionsContract(
                        name = "First",
                        collectionIds = listOf(CollectionId("A"), CollectionId("B"))
                    ),
                    ContractFactory.sampleSelectedVideosContract(
                        name = "Second",
                        videoIds = listOf(VideoId("C"), VideoId("D"))
                    )
                )
            )
            val user = insertTestUser(organisation)

            val contracts = client.getContracts(user.id)

            assertThat(contracts)
                .extracting("name")
                .containsExactlyInAnyOrder("First", "Second")

            val selectedCollectionsContracts = contracts.filterIsInstance<SelectedCollectionsContract>()
            assertThat(selectedCollectionsContracts)
                .flatExtracting("collectionIds")
                .containsExactlyInAnyOrder("A", "B")

            val selectedVideosContracts = contracts.filterIsInstance<SelectedVideosContract>()
            assertThat(selectedVideosContracts)
                .flatExtracting("videoIds")
                .containsExactlyInAnyOrder("C", "D")
        }
    }

    @Nested
    inner class GetOrganisationAccount {
        @Test
        fun `returns an organisation account`() {
            val organisationId = insertTestOrganisation(
                name = "overriding org",
                allowsOverridingUserIds = true
            )
            val user = insertTestUser(organisationId)

            val account = client.getAccount(organisationId)

            assertThat(account.organisation.allowsOverridingUserIds).isTrue()
        }
    }

    abstract fun insertTestOrganisation(
        name: String,
        contracts: List<DomainContract> = emptyList(),
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
        contracts: List<DomainContract>,
        allowsOverridingUserIds: Boolean
    ): String {
        return saveOrganisationWithContractDetails(name, contracts.toList(), allowsOverridingUserIds).id.value
    }

    override fun insertTestUser(organisationId: String, subjectId: String, shareCode: String): User {
        subjectService.addSubject(com.boclips.users.domain.model.Subject(id = SubjectId(subjectId), name = subjectId))

        val user = UserFactory.sample(
            organisationAccountId = AccountId(organisationId),
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
            user.organisationAccountId!!.value,
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
        contracts: List<DomainContract>,
        allowsOverridingUserIds: Boolean
    ): String {
        contracts.map { domainContract ->
            when (domainContract) {
                is DomainContract.SelectedCollections -> SelectedCollectionsContract().apply {
                    this.name = domainContract.name
                    this.collectionIds = domainContract.collectionIds.map { it.value }
                }
                is DomainContract.SelectedVideos -> SelectedVideosContract().apply {
                    this.name = domainContract.name
                    this.videoIds = domainContract.videoIds.map { it.value }
                }
            }
        }.forEach { convertedContract ->
            (client as FakeUserServiceClient).addContract(convertedContract)
        }

        val organisation = ClientOrganisation(allowsOverridingUserIds)
        val account = ClientAccount(name, organisation)
        (client as FakeUserServiceClient).addAccount(account)

        return account.id
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
