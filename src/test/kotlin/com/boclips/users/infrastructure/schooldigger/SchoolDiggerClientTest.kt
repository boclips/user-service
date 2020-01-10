package com.boclips.users.infrastructure.schooldigger

import com.boclips.users.domain.model.school.Country
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate
import java.util.regex.Pattern

class SchoolDiggerClientTest {
    // This are testing credentials that will return non production data
    val client = SchoolDiggerClient(SchoolDiggerProperties().apply {
        host = "https://api.schooldigger.com"
        applicationId = "50ff3e7e"
        applicationKey = "185957f0693951c7f0540822732839bb"
    }, RestTemplate())

    @Test
    fun `lookup results contain school id, name and city`() {
        val lookupSchools = client.lookupSchools("AK", "sc")

        assertThat(lookupSchools).isNotEmpty
        assertThat(lookupSchools.first().id).isNotBlank()
        assertThat(lookupSchools.first().name).isNotBlank()
        assertThat(lookupSchools.first().name).matches(Pattern.compile("^[\\w ]+, [\\w ]+$"))
    }
    @Test
    fun `lookup when failure returns empty array`() {
        val lookupSchools = client.lookupSchools("XXXXXX", "sc")

        assertThat(lookupSchools).isEmpty()
    }

    @Test
    fun fetchSchool() {
        val (school, district) = client.fetchSchool("020048000180")!!

        assertThat(school.name).isNotBlank()
        assertThat(school.externalId).isNotBlank()
        assertThat(school.state).isNotNull
        assertThat(school.country).isEqualTo(Country.usa())
        assertThat(school.postcode).isNotBlank()
        assertThat(district).isNotNull
        assertThat(district!!.externalId).isNotBlank()
        assertThat(district.name).isNotBlank()
        assertThat(district.country).isEqualTo(Country.usa())
        assertThat(district.state).isEqualTo(school.state)
    }

    @Test
    fun `fetch school when failure returns null`() {
        val school = client.fetchSchool("XXXX")

        assertThat(school).isNull()
    }
}
