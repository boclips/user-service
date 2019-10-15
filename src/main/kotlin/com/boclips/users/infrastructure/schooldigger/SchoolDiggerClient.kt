package com.boclips.users.infrastructure.schooldigger

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.service.AmericanSchoolsProvider
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

    override fun fetchSchool(schoolId: String): Pair<School, District?>? {
        val school = try {
            restTemplate.getForObject(
                buildSchoolEndpoint(schoolId),
                SchoolDiggerSchoolDetail::class.java
            )
        } catch (e: Exception) {
            logger.error("cannot fetch school $schoolId in schooldigger", e)
            return null
        }

        return school?.let { existingSchool ->
            School(
                name = existingSchool.schoolName,
                externalId = existingSchool.schoolid,
                state = State.fromCode(existingSchool.address.state),
                country = Country.usa(),
                district = null
            ) to existingSchool.district?.let { existingDistrict ->
                District(
                    name = existingDistrict.districtName,
                    externalId = existingDistrict.districtID,
                    state = State.fromCode(existingSchool.address.state)
                )
            }
        }
    }

    override fun lookupSchools(stateId: String, schoolName: String): List<LookupEntry> {
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

        return response.body?.schoolMatches
            ?.map { LookupEntry(it.schoolid, it.schoolName) }
            ?: emptyList()
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

