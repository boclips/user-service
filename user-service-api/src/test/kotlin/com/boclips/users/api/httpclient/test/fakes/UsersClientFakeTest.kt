package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.factories.AccessRuleResourceFactory
import com.boclips.users.api.factories.UserResourceFactory
import com.boclips.users.api.request.user.CreateUserRequest
import com.boclips.users.api.response.accessrule.AccessRuleResource
import com.boclips.users.api.response.accessrule.AccessRulesResource
import com.boclips.users.api.response.accessrule.AccessRulesWrapper
import com.boclips.users.api.response.feature.FeatureKeyResource
import feign.FeignException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UsersClientFakeTest {
    @Test
    fun `can fetch an access rule`() {
        val fake = UsersClientFake()
        fake.addAccessRules(
            userId = "hello",
            accessRulesResource = AccessRulesResource(
                _embedded = AccessRulesWrapper(
                    listOf(
                        AccessRuleResourceFactory.sampleIncludedCollections()
                    )
                )
            )
        )

        val accessRule = fake.getAccessRulesOfUser("hello")._embedded.accessRules.first()
        assertThat(accessRule)
            .isInstanceOf(AccessRuleResource.IncludedCollections::class.java)
    }

    @Test
    fun `can add multiple access rules to the same user`() {
        val fake = UsersClientFake()
        fake.addAccessRules(
            userId = "hello",
            accessRulesResource = AccessRulesResource(
                _embedded = AccessRulesWrapper(
                    listOf(
                        AccessRuleResourceFactory.sampleIncludedCollections()
                    )
                )
            )
        )

        fake.addAccessRules(
            "hello",
            accessRulesResource = AccessRulesResource(
                _embedded = AccessRulesWrapper(
                    listOf(
                        AccessRuleResourceFactory.sampleIncludedCollections()
                    )
                )
            )
        )

        val accessRules = fake.getAccessRulesOfUser("hello")._embedded.accessRules
        assertThat(accessRules).hasSize(2)
    }

    @Test
    fun `can resolve access rules based on client`() {
        val fake = UsersClientFake()
        fake.addAccessRules(
            userId = "my-user-id",
            accessRulesResource = AccessRulesResource(_embedded =
            AccessRulesWrapper(accessRules = listOf(AccessRuleResourceFactory.sampleIncludedCollections(collectionIds = listOf("collection-1-id"))))),
            client = "teachers"
        )
        fake.addAccessRules(
            userId = "my-user-id",
            accessRulesResource = AccessRulesResource(_embedded =
            AccessRulesWrapper(accessRules = listOf(AccessRuleResourceFactory.sampleExcludedVideos(videoIds = listOf("video-1-id"))))),
            client = "boclips-web-app"
        )

        val webAppRules = fake.getAccessRulesOfUser("my-user-id", client = "boclips-web-app")
        val teachersRules = fake.getAccessRulesOfUser("my-user-id", client = "teachers")

        assertThat(webAppRules._embedded.accessRules.size).isEqualTo(1)
        assertThat(teachersRules._embedded.accessRules.size).isEqualTo(1)

        assertThat(webAppRules._embedded.accessRules.first().type).isEqualTo("ExcludedVideos")
        assertThat(teachersRules._embedded.accessRules.first().type).isEqualTo("IncludedCollections")
    }

    @Test
    fun `can fetch a user`() {
        val fake = UsersClientFake()
        val user = UserResourceFactory.sample(id = "1", firstName = "Baptiste")
        fake.add(user)
        val userResource = fake.getUser("1")
        assertThat(userResource).isEqualTo(user)
    }

    @Test
    fun `can create api user`() {
        val fake = UsersClientFake()
        fake.createApiUser(
            CreateUserRequest.CreateApiUserRequest(
                apiUserId = "1",
                organisationId = "123",
                externalUserId = "external-user-id"
            )
        )
        val userResource = fake.getUser("1")
        assertThat(userResource.id).isEqualTo("1")
        assertThat(userResource.organisation!!.id).isEqualTo("123")
    }

    @Test
    fun `throws when invalid share code`() {
        assertThrows<FeignException.Forbidden> {
            UsersClientFake().getShareCode("wrong", "even more wrong")
        }
    }

    @Test
    fun `can fetch logged in user`() {
        val fake = UsersClientFake()

        fake.setLoggedInUser(
            UserResourceFactory.sample(
                firstName = "John",
                lastName = "Doe",
                id = "123",
                features = mapOf(FeatureKeyResource.LTI_COPY_RESOURCE_LINK to true)
            )
        )

        val loggedInUser = fake.getLoggedInUser()
        assertThat(loggedInUser.id).isEqualTo("123")
        assertThat(loggedInUser.firstName).isEqualTo("John")
        assertThat(loggedInUser.lastName).isEqualTo("Doe")
        assertThat(loggedInUser.features!![FeatureKeyResource.LTI_COPY_RESOURCE_LINK]).isEqualTo(true)
    }
}
