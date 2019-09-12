package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.testsupport.factories.OrganisationDocumentFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrganisationDocumentConverterTest {

    @Test
    fun `independent school`() {
        val organisationDocument = OrganisationDocumentFactory.sample(
            contractIds = listOf("A", "B"),
            name = "amazing school",
            type = OrganisationType.SCHOOL,
            externalId = "external-id",
            parentOrganisation = null
        )

        val organisationAccount = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(organisationAccount.id.value).isEqualTo(organisationDocument.id!!)
        assertThat(organisationAccount.contractIds).containsExactly(ContractId("A"), ContractId("B"))
        assertThat(organisationAccount.organisation).isInstanceOf(School::class.java)

        val independentSchool = organisationAccount.organisation as School
        assertThat(independentSchool.name).isEqualTo("amazing school")
        assertThat(independentSchool.country.id).isEqualTo(organisationDocument.country?.code)
        assertThat(independentSchool.state?.id).isEqualTo(organisationDocument.state?.code)
        assertThat(independentSchool.externalId).isEqualTo("external-id")

        assertThat(independentSchool.district).isNull()
    }

    @Test
    fun `school with district`() {
        val organisationDocument = OrganisationDocumentFactory.sample(
            contractIds = listOf("A", "B"),
            name = "amazing school",
            type = OrganisationType.SCHOOL,
            externalId = "external-id",
            parentOrganisation = OrganisationDocumentFactory.sample(
                name = "amazing district",
                type = OrganisationType.DISTRICT,
                externalId = "external-district-id"
            )
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(school.organisation).isInstanceOf(School::class.java)
        assertThat((school.organisation as School).district?.organisation).isInstanceOf(District::class.java)
    }
}
