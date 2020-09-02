package com.boclips.users.application

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
import java.time.LocalDate
import java.time.ZoneId

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
        val orgAccess = LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault())

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
        assertThat(argument.value.first().accessExpiresOn).isEqualTo(orgAccess.toInstant())
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
}
