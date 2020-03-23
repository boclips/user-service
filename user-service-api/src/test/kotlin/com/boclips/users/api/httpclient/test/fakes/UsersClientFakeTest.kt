package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.factories.AccessRuleResourceFactory
import com.boclips.users.api.factories.UserResourceFactory
import com.boclips.users.api.response.accessrule.AccessRuleResource
import com.boclips.users.api.response.accessrule.AccessRulesResource
import com.boclips.users.api.response.accessrule.AccessRulesWrapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UsersClientFakeTest {
    @Test
    fun `can fetch an access rule`() {
        val fake = UsersClientFake()
        fake.addAccessRules(
            userId = "hello", accessRulesResource = AccessRulesResource(
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
    fun `can fetch a user`() {
        val fake = UsersClientFake()
        val user = UserResourceFactory.sample(id = "1", firstName = "Baptiste")
        fake.add(user)
        val userResource = fake.getUser("1")
        assertThat(userResource).isEqualTo(user)
    }
}
