package com.boclips.users.presentation.hateoas

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SelectedAccessRuleLinkBuilderTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var selectedAccessRuleLinkBuilder: SelectedAccessRuleLinkBuilder

    @Test
    fun `add collection to access rule link`() {
        val accessRuleId = "contract-id"

        val link = selectedAccessRuleLinkBuilder.addCollection(accessRuleId)

        assertThat(link.rel.value()).isEqualTo("addCollection")
        assertThat(link.href).endsWith("/v1/selected-content-access-rules/$accessRuleId/collections/{collectionId}")
        assertThat(link.isTemplated).isTrue()
    }

    @Test
    fun `remove collection from access rule link`() {
        val accessRuleId = "contract-id"

        val link = selectedAccessRuleLinkBuilder.removeCollection(accessRuleId)

        assertThat(link.rel.value()).isEqualTo("removeCollection")
        assertThat(link.href).endsWith("/v1/selected-content-access-rules/$accessRuleId/collections/{collectionId}")
        assertThat(link.isTemplated).isTrue()
    }
}
