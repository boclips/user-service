package com.boclips.users.application.commands

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.LtiDeployment
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class SynchroniseIntegrationUserTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var synchroniseIntegrationUser: SynchroniseIntegrationUser

    var baseLtiOrganisation: Organisation = OrganisationFactory.apiIntegration()

    @BeforeEach
    fun setupUserAndOrganisation() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        baseLtiOrganisation = saveOrganisation(OrganisationFactory.apiIntegration(
            name = "lti-integration-organisation"
        ))
        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    username = "service-account-user",
                    id = userId,
                ),
                organisation = baseLtiOrganisation
            )
        )
    }

    @Test
    fun `it creates a new user under new deployment organisation`() {
        synchroniseIntegrationUser("deployment-id", "external-user-id")

        val organisations = organisationRepository.findOrganisationsByParentId(baseLtiOrganisation.id)
        assertThat(organisations.size).isEqualTo(1)
        assertThat(organisations[0].type()).isEqualTo(OrganisationType.LTI_DEPLOYMENT)
        assertThat((organisations[0] as LtiDeployment).deploymentId).isEqualTo("deployment-id")
        assertThat((organisations[0] as LtiDeployment).parent.name).isEqualTo("lti-integration-organisation")

        val users = userRepository.findAllByOrganisationId(organisations[0].id)
        assertThat(users.size).isEqualTo(1)
        assertThat(users[0].identity.username).isEqualTo("external-user-id")
    }

    @Test
    fun `creates only user when organisation already exists`() {
        saveOrganisation(OrganisationFactory.ltiDeployment(
            name = "lti-deployment-organisation",
            deploymentId = "deployment-id",
            parent = baseLtiOrganisation
        ))

        assertThat(organisationRepository.findOrganisationsByParentId(baseLtiOrganisation.id).size).isEqualTo(1)

        synchroniseIntegrationUser("deployment-id", "external-user-id")

        val organisations = organisationRepository.findOrganisationsByParentId(baseLtiOrganisation.id)
        assertThat(organisations.size).isEqualTo(1)
        val users = userRepository.findAllByOrganisationId(organisations[0].id)
        assertThat(users.size).isEqualTo(1)
        assertThat(users[0].identity.username).isEqualTo("external-user-id")
    }

    @Test
    fun `creates neither organisation nor user when both exist`() {
        val ltiDeploymentOrganisation = saveOrganisation(OrganisationFactory.ltiDeployment(
            name = "lti-deployment-organisation",
            deploymentId = "deployment-id",
            parent = baseLtiOrganisation
        ))

        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    username = "external-user-id",
                    id = "internal-user-id"
                ),
                organisation = ltiDeploymentOrganisation
            )
        )

        assertThat(organisationRepository.findOrganisationsByParentId(baseLtiOrganisation.id).size).isEqualTo(1)
        assertThat(userRepository.findAllByOrganisationId(ltiDeploymentOrganisation.id).size).isEqualTo(1)

        val user = synchroniseIntegrationUser("deployment-id", "external-user-id")

        assertThat(organisationRepository.findOrganisationsByParentId(baseLtiOrganisation.id).size).isEqualTo(1)
        assertThat(userRepository.findAllByOrganisationId(ltiDeploymentOrganisation.id).size).isEqualTo(1)
        assertThat(user.id.value).isEqualTo("internal-user-id")
    }

    @Test
    fun `it creates a new user when external id already exists in another deployment`() {
        val ltiDeploymentOrganisation = saveOrganisation(OrganisationFactory.ltiDeployment(
            name = "lti-deployment-organisation",
            deploymentId = "deployment-id",
            parent = baseLtiOrganisation
        ))

        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    username = "external-user-id",
                    id = "internal-user-id"
                ),
                organisation = ltiDeploymentOrganisation
            )
        )

        synchroniseIntegrationUser("deployment-id-1", "external-user-id")


        val users = userRepository.findAll().filter { it.identity.username == "external-user-id" }
        assertThat(users.size).isEqualTo(2)

        assertThat(users[0].organisation?.id?.value).isNotEqualTo(users[1].organisation?.id?.value)
    }
}