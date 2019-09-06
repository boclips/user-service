package com.boclips.users.client

import com.boclips.users.client.implementation.ApiUserServiceClient
import com.boclips.users.client.implementation.FakeUserServiceClient
import com.boclips.users.client.model.User
import com.boclips.users.client.model.contract.SelectedContentContract
import com.boclips.users.client.testsupport.AbstractClientIntegrationTest
import com.boclips.users.client.testsupport.config.ContractTestSecurityConfig.Companion.testPassword
import com.boclips.users.client.testsupport.config.ContractTestSecurityConfig.Companion.testUser
import com.boclips.users.domain.model.contract.CollectionId
import com.boclips.users.testsupport.factories.ContractFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder
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
            val organisationName = "test-organisation-id"
            val user = insertTestUser(organisationName)

            val retrievedUser = client.findUser(user.id)
            assertThat(retrievedUser.id).isEqualTo(user.id)
        }
    }

    @Nested
    inner class GetContractsOfUser {
        @Test
        fun `returns an empty list when user is not eligible to any contracts`() {
            val organisationName = "test-organisation-id"
            val user = insertTestUser(organisationName)

            assertThat(client.getContracts(user.id)).isEmpty()
        }

        @Test
        fun `returns a list of contracts user is eligible to`() {
            val organisationName = "test-organisation-id"
            val user = insertTestUser(
                organisationName,
                ContractFactory.sampleSelectedContentContract(
                    name = "First",
                    collectionIds = listOf(CollectionId("A"), CollectionId("B"))
                ),
                ContractFactory.sampleSelectedContentContract(
                    name = "Second",
                    collectionIds = listOf(CollectionId("C"), CollectionId("D"))
                )
            )

            assertThat(client.getContracts(user.id))
                .flatExtracting("collectionIds")
                .containsExactlyInAnyOrder("A", "B", "C", "D")
        }
    }

    abstract fun insertTestUser(organisationName: String, vararg contracts: DomainContract): User

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

    override fun insertTestUser(organisationName: String, vararg contracts: DomainContract): User {
        val organisation = saveOrganisationWithContractDetails(organisationName, contracts.toList())
        val user = UserFactory.sample(organisationAccountId = organisation.id)

        saveUser(user)

        return User(user.id.value, user.organisationAccountId!!.value)
    }
}

class FakeUserServiceClientContractTest : UserServiceClientContractTest() {
    @BeforeEach
    fun initialiseFakeClient() {
        client = FakeUserServiceClient()
    }

    override fun insertTestUser(organisationName: String, vararg contracts: DomainContract): User {
        contracts.forEach { domainContract ->
            domainContract as DomainContract.SelectedContent
            (client as FakeUserServiceClient).addContract(
                SelectedContentContract().apply {
                    name = domainContract.name
                    collectionIds = domainContract.collectionIds.map { it.value }
                }
            )
        }
        return (client as FakeUserServiceClient).addUser(User("idontcare", "scam"))
    }
}
