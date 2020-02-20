package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AccessRuleLinkBuilderTest : AbstractSpringIntegrationTest() {
    @Test
    fun `creates a self link to access rule`() {
        val accessRuleIdValue = "test-contract-id"

        val selfLink = accessRuleLinkBuilder.self(AccessRuleId(accessRuleIdValue))

        assertThat(selfLink.rel.value()).isEqualTo("self")
        assertThat(selfLink.href).endsWith("/v1/access-rules/$accessRuleIdValue")
    }

    @Nested
    inner class SearchAccessRules {
        @BeforeEach
        fun setupSecurityContext() {
            setSecurityContext("role-abiding-citizen", UserRoles.VIEW_ACCESS_RULES)
        }

        @Test
        fun `returns a templated link if name is not provided`() {
            val searchAccessRulesLink = accessRuleLinkBuilder.searchAccessRules()

            assertThat(searchAccessRulesLink!!.rel.value()).isEqualTo("searchAccessRules")
            assertThat(searchAccessRulesLink.isTemplated).isTrue()
            assertThat(searchAccessRulesLink.href).endsWith("/v1/access-rules{?name}")
        }

        @Test
        fun `returns a fixed link when name is provided`() {
            val searchAccessRulesLink = accessRuleLinkBuilder.searchAccessRules(name = "Hello world")

            assertThat(searchAccessRulesLink!!.rel.value()).isEqualTo("searchAccessRules")
            assertThat(searchAccessRulesLink.isTemplated).isFalse()
            assertThat(searchAccessRulesLink.href).endsWith("/v1/access-rules?name=Hello%20world")
        }

        @Test
        fun `returns null if user does not have VIEW_ACCESS_RULES role`() {
            setSecurityContext("rebel-without-a-role")

            val searchAccessRulesLink = accessRuleLinkBuilder.searchAccessRules()

            assertThat(searchAccessRulesLink).isNull()
        }

        @Test
        fun `allows to override rel`() {
            val searchAccessRulesLink = accessRuleLinkBuilder.searchAccessRules(rel = "self")

            assertThat(searchAccessRulesLink!!.rel.value()).isEqualTo("self")
            assertThat(searchAccessRulesLink.isTemplated).isTrue()
            assertThat(searchAccessRulesLink.href).endsWith("/v1/access-rules{?name}")
        }
    }
}
