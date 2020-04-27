package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.testsupport.factories.OrganisationDocumentFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class OrganisationDocumentConverterTest {

    @Test
    fun `independent school`() {
        val organisationDocument = OrganisationDocumentFactory.sample(
            name = "amazing school",
            domain = "independent.ac.co.uk",
            type = OrganisationType.SCHOOL,
            externalId = "external-id",
            dealType = null,
            accessExpiresOn = null
        )

        val organisationAccount = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(organisationAccount.id.value).isEqualTo(organisationDocument._id!!.toHexString())
        assertThat(organisationAccount.deal.type).isEqualTo(DealType.STANDARD)
        assertThat(organisationAccount).isInstanceOf(School::class.java)

        val independentSchool = organisationAccount as School
        assertThat(independentSchool.name).isEqualTo("amazing school")
        assertThat(independentSchool.domain).isEqualTo("independent.ac.co.uk")
        assertThat(independentSchool.address.country?.id).isEqualTo(organisationDocument.country?.code)
        assertThat(independentSchool.address.state?.id).isEqualTo(organisationDocument.state?.code)
        assertThat(independentSchool.externalId?.value).isEqualTo("external-id")
        assertThat(independentSchool.district).isNull()
        assertThat(organisationAccount.deal.accessExpiresOn).isNull()
    }

    @Test
    fun `school with district`() {

        val organisationDocument = OrganisationDocumentFactory.sample(
            name = "amazing school",
            domain = "school.com",
            type = OrganisationType.SCHOOL,
            dealType = null,
            externalId = "external-id",
            parent = OrganisationDocumentFactory.sample(
                type = OrganisationType.DISTRICT
            )
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument) as School

        assertThat(school.deal.type).isEqualTo(DealType.STANDARD)
        assertThat(school).isInstanceOf(School::class.java)
        assertThat(school.district?.deal?.type).isEqualTo(DealType.STANDARD)
        assertThat(school.district).isInstanceOf(District::class.java)
        assertThat(school.domain).isEqualTo("school.com")
    }

    @Test
    fun `design partner school`() {
        val accessExpiresOn = ZonedDateTime.now().plusMonths(3)

        val organisationDocument = OrganisationDocumentFactory.sample(
            type = OrganisationType.SCHOOL,
            dealType = null,
            domain = "design-partner.com",
            parent = OrganisationDocumentFactory.sample(
                type = OrganisationType.DISTRICT,
                dealType = DealType.DESIGN_PARTNER,
                accessExpiresOn = accessExpiresOn
            )
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument) as School

        assertThat(school.deal.type).isEqualTo(DealType.DESIGN_PARTNER)
        assertThat(school.district?.deal?.type).isEqualTo(DealType.DESIGN_PARTNER)
        assertThat(school.district?.deal?.accessExpiresOn).isEqualTo(accessExpiresOn)
        assertThat(school.domain).isEqualTo("design-partner.com")
    }

    @Test
    fun `symmetrical conversion`() {
        val organisation = OrganisationFactory.school()
        val organisationDocument = OrganisationDocumentConverter.toDocument(organisation)
        val convertedOrganisation =
            OrganisationDocumentConverter.fromDocument(organisationDocument = organisationDocument)

        assertThat(organisation).isEqualTo(convertedOrganisation)
    }

    @Test
    fun `parent document gets written as nested object`() {
        val districtId = OrganisationId()
        val district = OrganisationFactory.district(id = districtId)
        val school = OrganisationFactory.school(district = district)

        val schoolDocument = OrganisationDocumentConverter.toDocument(school)

        assertThat(schoolDocument.parent).isNotNull
        assertThat(schoolDocument.parent?._id?.toHexString()).isEqualTo(districtId.value)
    }
}
