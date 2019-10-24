package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.SearchSchools
import com.boclips.users.presentation.resources.school.SchoolResource
import org.springframework.hateoas.CollectionModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class OrganisationController(
    private val searchSchools: SearchSchools
) {
    @GetMapping("/schools")
    fun searchSchools(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) state: String?,
        @RequestParam(required = true) countryCode: String?
    ): CollectionModel<SchoolResource> {
        val schools = searchSchools(schoolName = query, state = state, countryCode = countryCode)

        return CollectionModel(
            schools.map { SchoolResource(id = it.id, name = it.name) })
    }
}
