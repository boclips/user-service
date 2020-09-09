package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class OrganisationDetailsRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `retrieve a child org with parent`() {
        val persistedParent = organisationRepository.save(OrganisationFactory.district())

        val persistedChild =
            organisationRepository.save(
                OrganisationFactory.school(
                    district = persistedParent
                )
            )

        assertThat(persistedChild.district).isEqualTo(persistedParent)

        val retrievedChild = organisationRepository.findSchoolById(persistedChild.id)!!
        assertThat(retrievedChild.district).isEqualTo(persistedParent)
    }

    @Test
    fun `findOrganisations sorts by expiry date first then alphabetically`() {
        val NOW = ZonedDateTime.now()
        organisationRepository.save(
            OrganisationFactory.school(
                name = "second",
                deal = deal(
                    accessExpiresOn = NOW
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "aaa",
                deal = deal(
                    accessExpiresOn = NOW.minusDays(1)
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "first",
                deal = deal(
                    accessExpiresOn = NOW
                )
            )
        )

        val results = organisationRepository.findOrganisations(
            name = null,
            countryCode = null,
            types = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
            page = 0,
            size = 6
        )

        assertThat(results.map { it.name }).containsExactly("first", "second", "aaa")
    }

    @Test
    fun `findOrganisations filters by country code`() {
        organisationRepository.save(
            OrganisationFactory.school(
                name = "second",
                address = Address(
                    country = Country.fromCode(Country.GBR_ISO)
                )
            )
        )
        organisationRepository.save(OrganisationFactory.school(name = "aaa"))

        val results = organisationRepository.findOrganisations(
            name = null,
            countryCode = "GBR",
            types = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
            page = 0,
            size = 6
        )

        assertThat(results.map { it.name }).containsExactly("second")
    }

    @Test
    fun `findOrganisations filters by organisation type`() {
        organisationRepository.save(OrganisationFactory.district(name = "second"))
        organisationRepository.save(OrganisationFactory.school(name = "aaa"))

        val results = organisationRepository.findOrganisations(
            name = null,
            countryCode = null,
            types = listOf(OrganisationType.SCHOOL),
            page = 0,
            size = 6
        )

        assertThat(results.map { it.name }).containsExactly("aaa")
    }

    @Test
    fun `findOrganisations retrieves both child and parent organisations`() {
        val persistedParent = organisationRepository.save(OrganisationFactory.district(name = "parent"))
        organisationRepository.save(
            OrganisationFactory.school(
                name = "child",
                district = persistedParent
            )
        )

        val results = organisationRepository.findOrganisations(
            name = null,
            countryCode = null,
            types = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
            page = 0,
            size = 6
        )

        assertThat(results.map { it.name }).containsExactly("child", "parent")
    }

    @Test
    fun `findParentOrganisations retrieves appropriate page with page info`() {
        val now = ZonedDateTime.now()
        organisationRepository.save(
            OrganisationFactory.school(
                name = "abc",
                deal = deal(
                    accessExpiresOn = now
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "def",
                deal = deal(
                    accessExpiresOn = now
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "xyz",
                deal = deal(
                    accessExpiresOn = now
                )
            )
        )

        val results = organisationRepository.findOrganisations(
            name = null,
            countryCode = null,
            types = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
            page = 1,
            size = 2
        )

        assertThat(results.map { it.name }).containsExactly("xyz")
    }
}
