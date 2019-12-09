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
    fun `parent organisations can be linked to children via a dbref`() {
        val persistedParent = repository.save(OrganisationDocumentFactory.sample(name = "parent"))

        val persistedChild = repository.save(OrganisationDocumentFactory.sample(name = "child", parentOrganisation = persistedParent))

        assertThat(persistedChild.parentOrganisation).isEqualTo(persistedParent)

        val retrievedChild: OrganisationDocument = repository.findById(persistedChild.id!!).get()
        assertThat(retrievedChild.parentOrganisation).isEqualTo(persistedParent)
    }
}
