package com.boclips.users.presentation.hateoas

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IncludedAccessRuleLinkBuilderTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var includedAccessRuleLinkBuilder: IncludedAccessRuleLinkBuilder

    @Test
    fun `add collection to access rule link`() {
        val accessRuleId = "contract-id"

        val link = includedAccessRuleLinkBuilder.addCollection(accessRuleId)

        assertThat(link.rel.value()).isEqualTo("addCollection")
        assertThat(link.href).endsWith("/v1/included-content-access-rules/$accessRuleId/collections/{collectionId}")
        assertThat(link.isTemplated).isTrue()
    }

    @Test
    fun `remove collection from access rule link`() {
        val accessRuleId = "contract-id"

        val link = includedAccessRuleLinkBuilder.removeCollection(accessRuleId)

        assertThat(link.rel.value()).isEqualTo("removeCollection")
        assertThat(link.href).endsWith("/v1/included-content-access-rules/$accessRuleId/collections/{collectionId}")
        assertThat(link.isTemplated).isTrue()
    }
}
