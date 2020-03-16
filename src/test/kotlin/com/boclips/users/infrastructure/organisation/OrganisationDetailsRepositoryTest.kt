package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.LocationDocumentFactory
import com.boclips.users.testsupport.factories.OrganisationDocumentFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime

class OrganisationDetailsRepositoryTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var repositorySpringDataMongo: SpringDataMongoOrganisationRepository

    @Test
    fun `parent organisations can be linked to children via a dbref`() {
        val persistedParent = repositorySpringDataMongo.save(OrganisationDocumentFactory.sample(name = "parent"))

        val persistedChild =
            repositorySpringDataMongo.save(
                OrganisationDocumentFactory.sample(
                    name = "child",
                    parentOrganisation = persistedParent
                )
            )

        assertThat(persistedChild.parentOrganisation).isEqualTo(persistedParent)

        val retrievedChild: OrganisationDocument = repositorySpringDataMongo.findById(persistedChild.id!!).get()
        assertThat(retrievedChild.parentOrganisation).isEqualTo(persistedParent)
    }

    @Test
    fun `findParentOrganisations sorts by expiry date first then alphabetically`() {
        val NOW = ZonedDateTime.now()
        repositorySpringDataMongo.save(OrganisationDocumentFactory.sample(name = "second", accessExpiresOn = NOW))
        repositorySpringDataMongo.save(
            OrganisationDocumentFactory.sample(
                name = "aaa",
                accessExpiresOn = NOW.minusDays(1)
            )
        )
        repositorySpringDataMongo.save(OrganisationDocumentFactory.sample(name = "first", accessExpiresOn = NOW))

        val results = repositorySpringDataMongo.findOrganisations(
            OrganisationSearchRequest(
                name = null,
                countryCode = "USA",
                organisationTypes = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
                page = 0,
                size = 6
            )
        )

        assertThat(results.get().map { it.name }).containsExactly("first", "second", "aaa")
    }

    @Test
    fun `findParentOrganisations filters by country code`() {
        repositorySpringDataMongo.save(
            OrganisationDocumentFactory.sample(
                name = "second",
                country = LocationDocumentFactory.country(code = "GBR")
            )
        )
        repositorySpringDataMongo.save(OrganisationDocumentFactory.sample(name = "aaa"))

        val results = repositorySpringDataMongo.findOrganisations(
            OrganisationSearchRequest(
                name = null,
                countryCode = "GBR",
                organisationTypes = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
                page = 0,
                size = 6
            )
        )

        assertThat(results.get().map { it.name }).containsExactly("second")
    }

    @Test
    fun `findParentOrganisations filters by organisation type`() {
        repositorySpringDataMongo.save(
            OrganisationDocumentFactory.sample(
                name = "second",
                type = OrganisationType.DISTRICT
            )
        )
        repositorySpringDataMongo.save(OrganisationDocumentFactory.sample(name = "aaa", type = OrganisationType.SCHOOL))

        val results = repositorySpringDataMongo.findOrganisations(
            OrganisationSearchRequest(
                name = null,
                countryCode = "USA",
                organisationTypes = listOf(OrganisationType.SCHOOL),
                page = 0,
                size = 6
            )
        )

        assertThat(results.get().map { it.name }).containsExactly("aaa")
    }

    @Test
    fun `findParentOrganisations retrieves parent organisations only`() {
        val persistedParent = repositorySpringDataMongo.save(OrganisationDocumentFactory.sample(name = "parent"))
        repositorySpringDataMongo.save(
            OrganisationDocumentFactory.sample(
                name = "child",
                parentOrganisation = persistedParent
            )
        )

        val results = repositorySpringDataMongo.findOrganisations(
            OrganisationSearchRequest(
                name = null,
                countryCode = "USA",
                organisationTypes = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
                page = 0,
                parentOnly = true,
                size = 6
            )
        )

        assertThat(results.get().map { it.name }).containsExactly("parent")
    }

    @Test
    fun `findParentOrganisations retrieves appropriate page with page info`() {
        val now = ZonedDateTime.now()
        repositorySpringDataMongo.save(OrganisationDocumentFactory.sample(name = "abc", accessExpiresOn = now))
        repositorySpringDataMongo.save(
            OrganisationDocumentFactory.sample(
                name = "xyz",
                accessExpiresOn = now.minusDays(1)
            )
        )
        repositorySpringDataMongo.save(OrganisationDocumentFactory.sample(name = "def", accessExpiresOn = now))

        val results = repositorySpringDataMongo.findOrganisations(
            OrganisationSearchRequest(
                name = null,
                countryCode = "USA",
                organisationTypes = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
                page = 1,
                parentOnly = true,
                size = 2
            )
        )

        assertThat(results.get().map { it.name }).containsExactly("xyz")
    }
}
