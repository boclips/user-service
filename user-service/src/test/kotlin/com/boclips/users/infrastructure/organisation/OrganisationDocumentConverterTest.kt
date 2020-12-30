package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationTag
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.organisation.VideoTypePrices
import com.boclips.users.testsupport.factories.OrganisationDocumentFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class OrganisationDocumentConverterTest {

    @Test
    fun `independent school`() {
        val organisationDocument = OrganisationDocumentFactory.sample(
            name = "amazing school",
            domain = "independent.ac.co.uk",
            type = OrganisationType.SCHOOL,
            externalId = "external-id",
            accessExpiresOn = null
        )

        val organisationAccount = OrganisationDocumentConverter.fromDocument(organisationDocument)

        assertThat(organisationAccount.id.value).isEqualTo(organisationDocument._id!!.toHexString())
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
            externalId = "external-id",
            parent = OrganisationDocumentFactory.sample(
                type = OrganisationType.DISTRICT
            )
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument) as School

        assertThat(school).isInstanceOf(School::class.java)
        assertThat(school.district).isInstanceOf(District::class.java)
        assertThat(school.domain).isEqualTo("school.com")
    }

    @Test
    fun `design partner school`() {
        val accessExpiresOn = ZonedDateTime.now().plusMonths(3)

        val organisationDocument = OrganisationDocumentFactory.sample(
            type = OrganisationType.SCHOOL,
            domain = "design-partner.com",
            parent = OrganisationDocumentFactory.sample(
                type = OrganisationType.DISTRICT,
                accessExpiresOn = accessExpiresOn
            )
        )

        val school = OrganisationDocumentConverter.fromDocument(organisationDocument) as School

        assertThat(school.district?.deal?.accessExpiresOn).isEqualTo(accessExpiresOn)
        assertThat(school.domain).isEqualTo("design-partner.com")
    }

    @Test
    fun `unrecognised tags are skipped`() {
        val document = OrganisationDocumentFactory.sample(
            tags = setOf("NOT_A_TAG")
        )

        val organisation = OrganisationDocumentConverter.fromDocument(document)

        assertThat(organisation.tags).isEmpty()
    }

    @Test
    fun `unset billing treated as no billing`() {
        val document = OrganisationDocumentFactory.sample(
            billing = null
        )

        val organisation = OrganisationDocumentConverter.fromDocument(document)

        assertThat(organisation.deal.billing).isFalse()
    }

    @Test
    fun `symmetrical conversion`() {
        val contentPackageId = ContentPackageId("some-id")
        val parentOrganisation = OrganisationFactory.district(
            tags = setOf(OrganisationTag.DESIGN_PARTNER),
            deal = deal(
                billing = true,
                contentPackageId = contentPackageId,
                prices = VideoTypePrices(
                    instructional = null,
                    news = BigDecimal.TEN,
                    stock = BigDecimal.ZERO
                )
            ),
            features = mapOf(Pair(Feature.LTI_COPY_RESOURCE_LINK, true))
        )
        val organisation = OrganisationFactory.school(
            district = parentOrganisation,
            deal = deal(
                prices = VideoTypePrices(
                    instructional = BigDecimal.ONE,
                    news = null,
                    stock = BigDecimal.ZERO
                )
            )
        )
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

    @Test
    fun `lti deployment symmetrical conversion`() {
        val ltiDeploymentId = OrganisationId()
        val ltiOrganisation = OrganisationFactory.ltiDeployment(id = ltiDeploymentId)
        val ltiDeployment = OrganisationFactory.ltiDeployment(id = ltiDeploymentId, parent = ltiOrganisation)

        val ltiDeploymentDocument = OrganisationDocumentConverter.toDocument(ltiDeployment)
        val ltiDeploymentConverted = OrganisationDocumentConverter.fromDocument(ltiDeploymentDocument)

        assertThat(ltiDeploymentConverted).isEqualTo(ltiDeployment)
    }

    @Nested
    inner class VideoTypePricesConversions {

        @Test
        fun `converts organisation domain object with a deal containing prices to a correct document`() {
            val parentOrganisation = OrganisationFactory.district(
                deal = deal(
                    prices = VideoTypePrices(
                        instructional = BigDecimal.ONE,
                        news = null,
                        stock = BigDecimal.ZERO
                    )
                )
            )
            val organisation = OrganisationFactory.school(
                district = parentOrganisation,
                deal = deal(
                    prices = VideoTypePrices(
                        instructional = BigDecimal.ONE,
                        news = BigDecimal.TEN,
                        stock = null
                    )
                )
            )
            val organisationDocument = OrganisationDocumentConverter.toDocument(organisation)

            assertThat(organisationDocument.parent!!.prices).isEqualTo(
                mapOf(
                    VideoTypeKey.INSTRUCTIONAL to BigDecimal.ONE,
                    VideoTypeKey.NEWS to null,
                    VideoTypeKey.STOCK to BigDecimal.ZERO
                )
            )

            assertThat(organisationDocument.prices).isEqualTo(
                mapOf(
                    VideoTypeKey.INSTRUCTIONAL to BigDecimal.ONE,
                    VideoTypeKey.NEWS to BigDecimal.TEN,
                    VideoTypeKey.STOCK to null
                )
            )
        }

        @Test
        fun `converts organisation domain object with a prices lacking deal to a correct document`() {
            val parentOrganisation = OrganisationFactory.district(
                deal = deal(
                    prices = null
                )
            )
            val organisation = OrganisationFactory.school(
                district = parentOrganisation,
                deal = deal(
                    prices = null
                )
            )
            val organisationDocument = OrganisationDocumentConverter.toDocument(organisation)

            assertThat(organisationDocument.parent!!.prices).isNull()
            assertThat(organisationDocument.prices).isNull()
        }

        @Test
        fun `converts organisation document with a prices containg deal to a correct domain object`() {
            val organisationDocument = OrganisationDocumentFactory.sample(
                type = OrganisationType.SCHOOL,
                prices = mapOf(
                    VideoTypeKey.INSTRUCTIONAL to BigDecimal.ONE,
                    VideoTypeKey.NEWS to BigDecimal.TEN,
                    VideoTypeKey.STOCK to null
                ),
                parent = OrganisationDocumentFactory.sample(
                    type = OrganisationType.DISTRICT,
                    prices = mapOf(
                        VideoTypeKey.INSTRUCTIONAL to BigDecimal.ONE,
                        VideoTypeKey.NEWS to null,
                        VideoTypeKey.STOCK to BigDecimal.ZERO
                    )
                )
            )

            val organisation = OrganisationDocumentConverter.fromDocument(organisationDocument)

            assertThat(organisation.deal.prices).isEqualTo(
                VideoTypePrices(
                    instructional = BigDecimal.ONE,
                    news = BigDecimal.TEN,
                    stock = null
                )
            )

            assertThat((organisation as School).district!!.deal.prices).isEqualTo(
                VideoTypePrices(
                    instructional = BigDecimal.ONE,
                    news = null,
                    stock = BigDecimal.ZERO
                )
            )
        }

        @Test
        fun `converts organisation document with a deal lacking prices to a correct domain object`() {
            val organisationDocument = OrganisationDocumentFactory.sample(
                type = OrganisationType.SCHOOL,
                prices = null,
                parent = OrganisationDocumentFactory.sample(
                    type = OrganisationType.DISTRICT,
                    prices = null
                )
            )

            val organisation = OrganisationDocumentConverter.fromDocument(organisationDocument)

            assertThat(organisation.deal.prices).isNull()
            assertThat((organisation as School).district!!.deal.prices).isNull()
        }
    }
}
