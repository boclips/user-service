package com.boclips.users.application

import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.marketing.CrmProfile
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.springframework.beans.factory.annotation.Autowired
import java.time.*

class SynchronisationServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var synchronisationService: SynchronisationService

    @Test
    fun `updates CRM profiles including session information`() {
        saveUser(UserFactory.sample())

        synchronisationService.synchroniseCrmProfiles()

        verify(marketingService).updateProfile(any())
    }

    @Test
    fun `updates CRM with correct expiry date`() {
        val userAccess = LocalDate.of(2015, 1, 1).atStartOfDay(ZoneId.systemDefault())
        val orgAccess = ZonedDateTime.now().plusDays(10)

        saveUser(
            UserFactory.sample(
                accessExpiresOn = userAccess,
                organisation = OrganisationFactory.school(
                    deal = OrganisationFactory.deal(
                        accessExpiresOn = orgAccess
                    )
                )
            )
        )

        synchronisationService.synchroniseCrmProfiles()

        val argument = ArgumentCaptor.forClass(List::class.java) as ArgumentCaptor<List<CrmProfile>>
        verify(marketingService).updateProfile(capture(argument))

        val receivedDate = localDate(argument.value.first().accessExpiresOn)
        val orgAccessDate = localDate(orgAccess.toInstant())

        assertThat(receivedDate).isEqualTo(orgAccessDate)
    }

    private fun localDate(instant: Instant?) =
        LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate()

    @Test
    fun `does not try to sync expired teachers or boclipper users`() {
        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(username = "teacher@test.com"),
                accessExpiresOn = ZonedDateTime.now().plusDays(1),
                organisation = OrganisationFactory.school()
            )
        )

        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(username = "expired_teacher@test.com"),
                organisation = OrganisationFactory.school(),
                accessExpiresOn = ZonedDateTime.now().minusDays(1)
            )
        )

        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(username = "boclipper@boclips.com"),
                organisation = OrganisationFactory.school(),
            )
        )

        synchronisationService.synchroniseCrmProfiles()

        val argument = ArgumentCaptor.forClass(List::class.java) as ArgumentCaptor<List<CrmProfile>>
        verify(marketingService).updateProfile(capture(argument))
        assertThat(argument.value.size).isEqualTo(1)
        assertThat(argument.value.first().email).isEqualTo("teacher@test.com")
    }

    @Test
    fun `updates new accounts from account provider`() {
        val existingIdentity = IdentityFactory.sample(id = "cat", roles = listOf("ROLE_TEACHER"))
        keycloakClientFake.createAccount(existingIdentity)
        saveUser(UserFactory.sample(id = existingIdentity.id.value))

        keycloakClientFake.createAccount(IdentityFactory.sample(id = "dog", roles = listOf("ROLE_TEACHER")))

        assertThat(userRepository.findAll()).hasSize(1)

        synchronisationService.synchroniseUserAccounts()

        assertThat(userRepository.findAll()).hasSize(2)
        assertThat(userRepository.findAll().map { it.id }).containsExactly(
            UserId(
                "cat"
            ), UserId("dog")
        )
    }

    @Test
    fun `updates user's features with organisaation features`() {
        val organisation = saveOrganisation(
            OrganisationFactory.ltiDeployment(
                features = mapOf(Feature.BO_WEB_APP_ADDITIONAL_SERVICES to true)
            )
        )
        val user = saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(username = "teacher@test.com"),
                accessExpiresOn = ZonedDateTime.now().plusDays(1),
                organisation = OrganisationFactory.ltiDeployment(
                    id = organisation.id,
                    features = mapOf(Feature.BO_WEB_APP_ADDITIONAL_SERVICES to false)
                )
            )
        )

        synchronisationService.synchroniseUsersOrganisations()

        val retrievedUser = userRepository.findById(user.id)

        assertThat(retrievedUser?.organisation?.features?.get(Feature.BO_WEB_APP_ADDITIONAL_SERVICES)).isTrue
    }
}
