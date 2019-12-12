package com.boclips.users.presentation.hateoas

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SchoolLinkBuilderTest {
    lateinit var schoolLinkBuilder: SchoolLinkBuilder

    @BeforeEach
    fun setUp() {
        schoolLinkBuilder = SchoolLinkBuilder()
    }

    @Test
    fun `expose school link`() {
        val schoolLink = schoolLinkBuilder.getSchoolLink("USA")

        assertThat(schoolLink).isNotNull
        assertThat(schoolLink!!.href).endsWith("/schools?countryCode=USA{&query,state}")
    }
}
