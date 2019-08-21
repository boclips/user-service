package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.Platform
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RoleBasedPlatformResolverTest : AbstractSpringIntegrationTest() {
    @Test
    fun `matches a teacher user to "Boclips for Teachers"`() {
        val userSource = userSourceResolver.resolve(listOf("ROLE_TEACHER"))

        assertThat(userSource).isInstanceOf(Platform.BoclipsForTeachers::class.java)
    }

    @Test
    fun `matches a viewsonic user to "ViewSonic MyViewBoard"`() {
        val userSource = userSourceResolver.resolve(listOf("ROLE_VIEWSONIC"))

        assertThat(userSource).isInstanceOf(Platform.ApiCustomer::class.java)
        assertThat((userSource as Platform.ApiCustomer).organisationId).isNotNull
    }

    @Test
    fun `matches a pearson user to "Pearson MyRealize"`() {
        val userSource = userSourceResolver.resolve(listOf("ROLE_PEARSON_MYREALIZE"))

        assertThat(userSource).isInstanceOf(Platform.ApiCustomer::class.java)
        assertThat((userSource as Platform.ApiCustomer).organisationId).isNotNull
    }

    @Test
    fun `does not match if there are only unknown roles`() {
        val matched = userSourceResolver.resolve(listOf("ROLE_STUDENT", "uma_offline", "Matt"))

        assertThat(matched).isNull()
    }

    @Test
    fun `can deal with no roles entities`() {
        val matched = userSourceResolver.resolve(emptyList())

        assertThat(matched).isNull()
    }

    @BeforeEach
    fun insertFixtures() {
        organisationRepository.save("ViewSonic MyViewBoard", "ROLE_VIEWSONIC")
        organisationRepository.save("Pearson MyRealize", "ROLE_PEARSON_MYREALIZE")
    }
}
