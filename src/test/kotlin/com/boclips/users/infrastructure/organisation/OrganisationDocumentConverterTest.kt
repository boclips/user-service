package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.testsupport.factories.OrganisationDocumentFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class OrganisationDocumentConverterTest {

    @Test
    fun `independent school`() {
        val organisationDocument = OrganisationDocumentFactory.sample(
            accessRuleIds = listOf("A", "B"),
            name = "amazing school",
            type = OrganisationType.SCHOOL,
            externalId = "external-id",
            dealType = null,
            parentOrganisation = null,
            accessExpiresOn = null
        )

        val organisationAccount = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(organisationAccount.id.value).isEqualTo(organisationDocument.id!!)
        assertThat(organisationAccount.type).isEqualTo(DealType.STANDARD)
        assertThat(organisationAccount.accessRuleIds).containsExactly(AccessRuleId("A"), AccessRuleId("B"))
        assertThat(organisationAccount.organisation).isInstanceOf(School::class.java)

        val independentSchool = organisationAccount.organisation as School
        assertThat(independentSchool.name).isEqualTo("amazing school")
        assertThat(independentSchool.country.id).isEqualTo(organisationDocument.country?.code)
        assertThat(independentSchool.state?.id).isEqualTo(organisationDocument.state?.code)
        assertThat(independentSchool.externalId).isEqualTo("external-id")

        assertThat(independentSchool.district).isNull()

        assertThat(organisationAccount.accessExpiresOn).isNull()
    }

    @Test
    fun `school with district`() {
        val organisationDocument = OrganisationDocumentFactory.sample(
            accessRuleIds = listOf("A", "B"),
            name = "amazing school",
            type = OrganisationType.SCHOOL,
            dealType = null,
            externalId = "external-id",
            parentOrganisation = OrganisationDocumentFactory.sample(
                name = "amazing district",
                type = OrganisationType.DISTRICT,
                dealType = null,
                externalId = "external-district-id"
            )
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(school.type).isEqualTo(DealType.STANDARD)
        assertThat(school.organisation).isInstanceOf(School::class.java)
        assertThat((school.organisation as School).district?.type).isEqualTo(DealType.STANDARD)
        assertThat((school.organisation as School).district?.organisation).isInstanceOf(District::class.java)
    }

    @Test
    fun `design partner school`() {
        val accessExpiresOn = ZonedDateTime.now().plusMonths(3)

        val organisationDocument = OrganisationDocumentFactory.sample(
            type = OrganisationType.SCHOOL,
            dealType = null,
            parentOrganisation = OrganisationDocumentFactory.sample(
                type = OrganisationType.DISTRICT,
                dealType = DealType.DESIGN_PARTNER,
                accessExpiresOn = accessExpiresOn
            )
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(school.type).isEqualTo(DealType.DESIGN_PARTNER)
        assertThat((school.organisation as School).district?.type).isEqualTo(DealType.DESIGN_PARTNER)
        assertThat((school.organisation as School).district?.accessExpiresOn).isEqualTo(accessExpiresOn)
    }
}
