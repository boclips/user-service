package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationDocumentFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
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
            parentOrganisationId = null,
            accessExpiresOn = null
        )

        val organisationAccount = OrganisationDocumentConverter.fromDocument(organisationDocument, null)

        assertThat(organisationAccount.id.value).isEqualTo(organisationDocument._id!!.toHexString())
        assertThat(organisationAccount.type).isEqualTo(DealType.STANDARD)
        assertThat(organisationAccount.details).isInstanceOf(School::class.java)

        val independentSchool = organisationAccount.details as School
        assertThat(independentSchool.name).isEqualTo("amazing school")
        assertThat(independentSchool.domain).isEqualTo("independent.ac.co.uk")
        assertThat(independentSchool.country.id).isEqualTo(organisationDocument.country?.code)
        assertThat(independentSchool.state?.id).isEqualTo(organisationDocument.state?.code)
        assertThat(independentSchool.externalId).isEqualTo("external-id")

        assertThat(independentSchool.district).isNull()

        assertThat(organisationAccount.accessExpiresOn).isNull()
    }

    @Test
    fun `school with district`() {
        val parentOrganisationDocument = OrganisationDocumentFactory.sample(
            id = ObjectId(),
            name = "amazing district",
            type = OrganisationType.DISTRICT,
            dealType = null,
            externalId = "external-district-id"
        )

        val organisationDocument = OrganisationDocumentFactory.sample(
            name = "amazing school",
            domain = "school.com",
            type = OrganisationType.SCHOOL,
            dealType = null,
            externalId = "external-id",
            parentOrganisationId = parentOrganisationDocument._id!!
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument, parentOrganisationDocument)

        assertThat(school.type).isEqualTo(DealType.STANDARD)
        assertThat(school.details).isInstanceOf(School::class.java)
        assertThat((school.details as School).district?.type).isEqualTo(DealType.STANDARD)
        assertThat((school.details as School).district?.details).isInstanceOf(District::class.java)
        assertThat(school.details.domain).isEqualTo("school.com")
    }

    @Test
    fun `design partner school`() {
        val accessExpiresOn = ZonedDateTime.now().plusMonths(3)

        val parentOrganisationDocument = OrganisationDocumentFactory.sample(
            type = OrganisationType.DISTRICT,
            dealType = DealType.DESIGN_PARTNER,
            accessExpiresOn = accessExpiresOn
        )

        val organisationDocument = OrganisationDocumentFactory.sample(
            type = OrganisationType.SCHOOL,
            dealType = null,
            domain = "design-partner.com",
            parentOrganisationId = parentOrganisationDocument._id
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument, parentOrganisationDocument)

        assertThat(school.type).isEqualTo(DealType.DESIGN_PARTNER)
        assertThat((school.details as School).district?.type).isEqualTo(DealType.DESIGN_PARTNER)
        assertThat((school.details as School).district?.accessExpiresOn).isEqualTo(accessExpiresOn)
        assertThat(school.details.domain).isEqualTo("design-partner.com")
    }

    @Test
    fun `symmetrical conversion`() {
        val organisation = OrganisationFactory.school()
        val (organisationDocument, parentOrganisationDocument) = OrganisationDocumentConverter.toDocument(organisation)
        val convertedOrganisation =
            OrganisationDocumentConverter.fromDocument(organisationDocument = organisationDocument, parentOrganisationDocument = parentOrganisationDocument)

        assertThat(organisation).isEqualTo(convertedOrganisation)
    }

    @Test
    fun `parent document gets written as nested object`() {
        val districtId = OrganisationId()
        val district = OrganisationFactory.district(id = districtId)
        val school = OrganisationFactory.school(
            school = OrganisationDetailsFactory.school(
                district = district
            )
        )

        val schoolDocument = OrganisationDocumentConverter.toDocument(school).organisation

        assertThat(schoolDocument.parent).isNotNull
        assertThat(schoolDocument.parent?._id?.toHexString()).isEqualTo(districtId.value)
    }
}
