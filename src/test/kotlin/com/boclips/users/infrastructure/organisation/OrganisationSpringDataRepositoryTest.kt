package com.boclips.users.infrastructure.organisation

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDocumentFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OrganisationSpringDataRepositoryTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var repository: OrganisationSpringDataRepository

    @Test
    fun `dbref links organisations`() {
        var parent = OrganisationDocumentFactory.sample(name = "parent")
        parent = repository.save(parent)
        val organisationDocument = repository.save(OrganisationDocumentFactory.sample(name = "child", parentOrganisation = parent))

        assertThat(organisationDocument.parentOrganisation).isEqualTo(parent)
        assertThat(repository.findById(organisationDocument.id!!).get().parentOrganisation).isEqualTo(parent)
    }
}