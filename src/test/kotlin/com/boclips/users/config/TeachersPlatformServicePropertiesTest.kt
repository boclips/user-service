package com.boclips.users.config

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TeachersPlatformServicePropertiesTest : AbstractSpringIntegrationTest() {
    @Test
    fun `configuration properties are initialised`() {
        assertThat(userServiceProperties.organisationMappings.component1().role).isEqualTo("ROLE_TEACHER")
        assertThat(userServiceProperties.organisationMappings.component1().organisationName).isEqualTo("Boclips for Teachers")
    }
}
