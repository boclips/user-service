package com.boclips.users.application.commands

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.UniqueId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AssignUsersByOrganisationDomainTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var assignUsersByOrganisationDomain: AssignUsersByOrganisationDomain

    @Test
    fun `assigns users that have the same domain but are not linked directly or indirectly to the organisation`() {
        val user = createUser(organisationId = UniqueId(), username = "rebecca@district-domain.com")

        val district =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district(domain = "district-domain.com")))

        val changedUsers = assignUsersByOrganisationDomain(district.id.value)

        assertThat(changedUsers.size).isEqualTo(1)
        assertThat(changedUsers[0].id).isEqualTo(user.id)
        assertThat(changedUsers[0].organisationId).isEqualTo(district.id)
    }

    @Test
    fun `does not update users that already belongs to an organisation indirectly (being a sub organisation)`() {
        val district =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district(domain = "district-domain.com")))

        val school =
            organisationRepository.save(
                OrganisationFactory.sample(
                    details = OrganisationDetailsFactory.school(
                        name = "a school",
                        district = district
                    )
                )
            )

        createUser(organisationId = school.id.value, username = "rebecca@district-domain.com")

        val changedUsers = assignUsersByOrganisationDomain(district.id.value)

        assertThat(changedUsers.size).isEqualTo(0)
    }

    @Test
    fun `assigns users that share the domain of an organisation but are not linked to that organisation`() {
        val user = createUser(username = "rebecca@district-domain.com")

        val district =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district(domain = "district-domain.com")))

        val changedUsers = assignUsersByOrganisationDomain(district.id.value)

        assertThat(changedUsers.size).isEqualTo(1)
        assertThat(changedUsers[0].id).isEqualTo(user.id)
        assertThat(changedUsers[0].organisationId).isEqualTo(district.id)
    }

    @Test
    fun `do not change users of different domains`() {
        val user = createUser(username = "rebecca@another-domain.com")

        val district =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district(domain = "district-domain.com")))

        val changedUsers = assignUsersByOrganisationDomain(district.id.value)

        assertThat(changedUsers.size).isEqualTo(0)
        assertThat(userRepository.findById(user.id)!!.organisationId).isNull()
    }

    @Test
    fun `does not assign users to organisations without domain information`() {
        val district =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district(domain = null)))

        val changedUsers = assignUsersByOrganisationDomain(district.id.value)

        assertThat(changedUsers.size).isEqualTo(0)
    }

    private fun createUser(organisationId: String? = null, username: String): User {
        val nullableOrganisationId = organisationId?.let {
            OrganisationId(organisationId)
        }

        return userRepository.create(
            user = UserFactory.sample(
                identity = IdentityFactory.sample(username = username),
                organisationId = nullableOrganisationId
            )
        )
    }
}
