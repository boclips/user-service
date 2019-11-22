package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.OrganisationAccountType
import com.boclips.users.domain.model.organisation.OrganisationType
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
            accountType = null,
            parentOrganisation = null
        )

        val organisationAccount = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(organisationAccount.id.value).isEqualTo(organisationDocument.id!!)
        assertThat(organisationAccount.type).isEqualTo(OrganisationAccountType.STANDARD)
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
            accountType = null,
            externalId = "external-id",
            parentOrganisation = OrganisationDocumentFactory.sample(
                name = "amazing district",
                type = OrganisationType.DISTRICT,
                accountType = null,
                externalId = "external-district-id"
            )
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(school.type).isEqualTo(OrganisationAccountType.STANDARD)
        assertThat(school.organisation).isInstanceOf(School::class.java)
        assertThat((school.organisation as School).district?.type).isEqualTo(OrganisationAccountType.STANDARD)
        assertThat((school.organisation as School).district?.organisation).isInstanceOf(District::class.java)
    }

    @Test
    fun `design partner school`() {
        val organisationDocument = OrganisationDocumentFactory.sample(
            type = OrganisationType.SCHOOL,
            accountType = null,
            parentOrganisation = OrganisationDocumentFactory.sample(
                type = OrganisationType.DISTRICT,
                accountType = OrganisationAccountType.DESIGN_PARTNER
            )
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(school.type).isEqualTo(OrganisationAccountType.DESIGN_PARTNER)
        assertThat((school.organisation as School).district?.type).isEqualTo(OrganisationAccountType.DESIGN_PARTNER)
    }
}
