package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ContractsLinkBuilderTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var contractsLinkBuilder: ContractsLinkBuilder

    @Test
    fun `produces a self link if user has VIEW_CONTRACTS role`() {
        val userId = "user"
        setSecurityContext(userId, UserRoles.VIEW_CONTRACTS)

        val contractsSelfLink = contractsLinkBuilder.self(UserId(userId))

        Assertions.assertThat(contractsSelfLink!!.href).endsWith("/v1/users/$userId/contracts")
        Assertions.assertThat(contractsSelfLink.rel).isEqualTo("self")
    }

    @Test
    fun `returns null if user does not have VIEW_CONTRACTS role`() {
        val userId = "user w/o required role"
        setSecurityContext(userId)

        assertThat(contractsLinkBuilder.self(UserId(userId))).isNull()
    }
}
