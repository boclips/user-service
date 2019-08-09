package com.boclips.users.infrastructure.user

import com.boclips.users.testsupport.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrganisationDocumentConverterTest {
    @Test
    fun `converts organisation back and forth`() {
        val organisation = OrganisationFactory.sample()

        val reConverted = OrganisationDocumentConverter.fromDocument(
            OrganisationDocumentConverter.toDocument(organisation)
        )

        assertThat(reConverted).isEqualTo(organisation)
    }
}
