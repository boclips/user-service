package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserContractLinkBuilderTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var userContractsLinkBuilder: UserContractsLinkBuilder

    @Test
    fun `produces a self link if user has VIEW_CONTRACTS role`() {
        val userId = "user"
        setSecurityContext(userId, UserRoles.VIEW_CONTRACTS)

        val contractsSelfLink = userContractsLinkBuilder.self(UserId(userId))

        assertThat(contractsSelfLink!!.href).endsWith("/v1/users/$userId/contracts")
        assertThat(contractsSelfLink.rel).isEqualTo("self")
    }

    @Test
    fun `returns null if user does not have VIEW_CONTRACTS role`() {
        val userId = "user w/o required role"
        setSecurityContext(userId)

        assertThat(userContractsLinkBuilder.self(UserId(userId))).isNull()
    }
}
