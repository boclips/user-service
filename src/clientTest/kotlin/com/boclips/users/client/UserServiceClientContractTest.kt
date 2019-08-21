package com.boclips.users.client

import com.boclips.users.client.implementation.ApiUserServiceClient
import com.boclips.users.client.implementation.FakeUserServiceClient
import com.boclips.users.client.model.User
import com.boclips.users.client.testsupport.AbstractClientIntegrationTest
import com.boclips.users.client.testsupport.config.ContractTestSecurityConfig.Companion.testPassword
import com.boclips.users.client.testsupport.config.ContractTestSecurityConfig.Companion.testUser
import com.boclips.users.domain.model.UserSource
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder

abstract class UserServiceClientContractTest : AbstractClientIntegrationTest() {
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
        assertThat(retrievedUser.organisationId).isNotBlank()
    }

    abstract fun insertTestUser(organisationName: String): User

    lateinit var client: UserServiceClient
}

class ApiUserServiceClientContractTest : UserServiceClientContractTest() {
    @BeforeEach
    fun initialiseApiClient() {
        client = ApiUserServiceClient(
            RestTemplateBuilder()
                .basicAuthentication(testUser, testPassword)
                .rootUri(userServiceUrl())
                .build()
        )
    }

    override fun insertTestUser(organisationName: String): User {
        val organisation = saveOrganisation(organisationName)
        val user = UserFactory.sample(
            account = AccountFactory.sample(
                associatedTo = UserSource.ApiClient(organisation.id)
            )
        )
        saveUser(user)

        return User(user.id.value, (user.account.associatedTo as UserSource.ApiClient).organisationId.value)
    }
}

class FakeUserServiceClientContractTest : UserServiceClientContractTest() {
    @BeforeEach
    fun initialiseFakeClient() {
        client = FakeUserServiceClient()
    }

    override fun insertTestUser(organisationName: String): User {
        return (client as FakeUserServiceClient).addUser(User("idontcare", "scam"))
    }
}
