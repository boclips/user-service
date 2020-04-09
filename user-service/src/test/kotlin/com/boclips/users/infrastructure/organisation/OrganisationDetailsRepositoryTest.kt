package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class OrganisationDetailsRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `retrieve a child org with parent`() {
        val persistedParent = organisationRepository.save(OrganisationFactory.district())

        val persistedChild =
            organisationRepository.save(
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.school(
                        district = persistedParent
                    )
                )
            )

        assertThat(persistedChild.details.district).isEqualTo(persistedParent)

        val retrievedChild = organisationRepository.findSchoolById(persistedChild.id)!!
        assertThat(retrievedChild.details.district).isEqualTo(persistedParent)
    }

    @Test
    fun `findOrganisations sorts by expiry date first then alphabetically`() {
        val NOW = ZonedDateTime.now()
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(name = "second"),
                accessExpiresOn = NOW
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(name = "aaa"),
                accessExpiresOn = NOW.minusDays(1)
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(name = "first"),
                accessExpiresOn = NOW
            )
        )

        val results = organisationRepository.findOrganisations(
            name = null,
            countryCode = "USA",
            types = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
            page = 0,
            size = 6
        )

        assertThat(results.map { it.details.name }).containsExactly("first", "second", "aaa")
    }

    @Test
    fun `findOrganisations filters by country code`() {
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "second",
                    country = Country.fromCode(Country.GBR_ISO)
                )
            )
        )
        organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.school(name = "aaa")))

        val results = organisationRepository.findOrganisations(
            name = null,
            countryCode = "GBR",
            types = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
            page = 0,
            size = 6
        )

        assertThat(results.map { it.details.name }).containsExactly("second")
    }

    @Test
    fun `findOrganisations filters by organisation type`() {
        organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district(name = "second")))
        organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.school(name = "aaa")))

        val results = organisationRepository.findOrganisations(
            name = null,
            countryCode = "USA",
            types = listOf(OrganisationType.SCHOOL),
            page = 0,
            size = 6
        )

        assertThat(results.map { it.details.name }).containsExactly("aaa")
    }

    @Test
    fun `findOrganisations retrieves parent organisations only`() {
        val persistedParent =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district(name = "parent")))
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "child",
                    district = persistedParent
                )
            )
        )

        val results = organisationRepository.findOrganisations(
            name = null,
            countryCode = "USA",
            types = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
            page = 0,
            size = 6
        )

        assertThat(results.map { it.details.name }).containsExactly("parent")
    }

    @Test
    fun `findParentOrganisations retrieves appropriate page with page info`() {
        val now = ZonedDateTime.now()
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(name = "abc"),
                accessExpiresOn = now
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(name = "def"),
                accessExpiresOn = now
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(name = "xyz"),
                accessExpiresOn = now
            )
        )

        val results = organisationRepository.findOrganisations(
            name = null,
            countryCode = "USA",
            types = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
            page = 1,
            size = 2
        )

        assertThat(results.map { it.details.name }).containsExactly("xyz")
    }
}
