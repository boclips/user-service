package com.boclips.users.infrastructure.schooldigger

import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.ExternalOrganisationInformation
import com.boclips.users.domain.model.organisation.ExternalSchoolInformation
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.service.organisation.AmericanSchoolsProvider
import mu.KLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class SchoolDiggerClient(
    val properties: SchoolDiggerProperties,
    private val restTemplate: RestTemplate
) : AmericanSchoolsProvider {
    companion object : KLogging()

    override fun fetchSchool(schoolId: String): ExternalSchoolInformation? {
        val diggerSchool = try {
            restTemplate.getForObject(
                buildSchoolEndpoint(schoolId),
                SchoolDiggerSchoolDetail::class.java
            ) ?: return null
        } catch (e: Exception) {
            logger.error("cannot fetch school $schoolId in schooldigger", e)
            return null
        }

        val school = ExternalOrganisationInformation(
            id = ExternalOrganisationId(diggerSchool.schoolid),
            name = diggerSchool.schoolName,
            address = Address(
                state = State.fromCode(diggerSchool.address.state),
                postcode = diggerSchool.address.zip,
                country = Country.usa()
            )
        )

        val district = diggerSchool.district?.let { diggerDistrict ->
            ExternalOrganisationInformation(
                id = ExternalOrganisationId(diggerDistrict.districtID),
                name = diggerDistrict.districtName,
                address = Address(
                    country = school.address.country,
                    state = school.address.state
                )
            )
        }

        return ExternalSchoolInformation(school, district)
    }

    override fun lookupSchools(stateId: String, schoolName: String): List<ExternalOrganisationInformation> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)

        val httpEntity = HttpEntity<String>(HttpHeaders())

        val response = try {
            restTemplate.exchange(
                buildSearchSchoolsEndpoint(stateId, schoolName),
                HttpMethod.GET,
                httpEntity,
                SchoolDiggerMatchesResponse::class.java
            )
        } catch (e: Exception) {
            logger.error("cannot lookup schools in schooldigger", e)
            return emptyList()
        }

        return response.body?.schoolMatches.orEmpty()
            .map {
                ExternalOrganisationInformation(
                    id = ExternalOrganisationId(it.schoolid),
                    name = "${it.schoolName}, ${it.city}",
                    address = Address()
                )
            }
    }

    private fun buildSchoolEndpoint(schoolId: String): URI {
        return UriComponentsBuilder
            .fromUriString("${properties.host}/v1.2/schools")
            .pathSegment(schoolId)
            .addKey()
            .build()
            .toUri()
    }

    private fun buildSearchSchoolsEndpoint(state: String, school: String): URI {
        return UriComponentsBuilder
            .fromUriString("${properties.host}/v1.2/autocomplete/schools")
            .queryParam("q", school)
            .queryParam("st", state)
            .addKey()
            .build()
            .toUri()
    }

    private fun UriComponentsBuilder.addKey(): UriComponentsBuilder {
        this.queryParam("appID", properties.applicationId)
        this.queryParam("appKey", properties.applicationKey)
        return this
    }
}

