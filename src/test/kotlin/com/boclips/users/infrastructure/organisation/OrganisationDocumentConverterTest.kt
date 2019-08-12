package com.boclips.users.infrastructure.organisation

import com.boclips.users.testsupport.OrganisationDocumentFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrganisationDocumentConverterTest {
    @Test
    fun `converts organisation document to organisation`() {
        val organisationDocument = OrganisationDocumentFactory.sample()

        val organisation = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(organisation.id.value).isEqualTo(organisationDocument.id.toHexString())
        assertThat(organisation.name).isEqualTo(organisationDocument.name)
    }
}
