package com.boclips.users.presentation.controllers

import com.boclips.users.api.response.school.SchoolResource
import com.boclips.users.api.response.school.SchoolsResource
import com.boclips.users.api.response.school.SchoolsWrapperResource
import com.boclips.users.application.commands.SearchSchools
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class SchoolController(private val searchSchools: SearchSchools) {
    @GetMapping("/schools")
    fun searchSchools(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) state: String?,
        @RequestParam(required = true) countryCode: String?
    ): SchoolsResource {
        val schools = searchSchools(schoolName = query, state = state, countryCode = countryCode)

        return SchoolsResource(
            _embedded = SchoolsWrapperResource(
                schools = schools.map {
                    SchoolResource(
                        id = it.id.value,
                        name = it.name
                    )
                }
            ),
            _links = null
        )
    }
}
