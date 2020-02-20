package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserAccessRuleLinkBuilderTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var userAccessRulesLinkBuilder: UserAccessRulesLinkBuilder

    @Test
    fun `produces a self link if user has VIEW_ACCESS_RULES role`() {
        val userId = "user"
        setSecurityContext(userId, UserRoles.VIEW_ACCESS_RULES)

        val accessRulesSelfLink = userAccessRulesLinkBuilder.self(UserId(userId))

        assertThat(accessRulesSelfLink!!.href).endsWith("/v1/users/$userId/access-rules")
        assertThat(accessRulesSelfLink.rel.value()).isEqualTo("self")
    }

    @Test
    fun `returns null if user does not have VIEW_ACCESS_RULES role`() {
        val userId = "user w/o required role"
        setSecurityContext(userId)

        assertThat(userAccessRulesLinkBuilder.self(UserId(userId))).isNull()
    }
}
