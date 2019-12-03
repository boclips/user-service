package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.SearchOrganisations
import com.boclips.users.application.commands.SearchSchools
import com.boclips.users.presentation.resources.OrganisationAccountResource
import com.boclips.users.presentation.resources.OrganisationResource
import com.boclips.users.presentation.resources.school.CountryResource
import com.boclips.users.presentation.resources.school.SchoolResource
import com.boclips.users.presentation.resources.school.StateResource
import org.springframework.hateoas.Resources
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class OrganisationController(
    private val searchSchools: SearchSchools,
    private val searchOrganisations: SearchOrganisations
) {
    @GetMapping("/schools")
    fun searchSchools(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) state: String?,
        @RequestParam(required = true) countryCode: String?
    ): Resources<SchoolResource> {
        val schools = searchSchools(schoolName = query, state = state, countryCode = countryCode)

        return Resources(
            schools.map { SchoolResource(id = it.id, name = it.name) })
    }

    @GetMapping("/organisations")
    fun searchOrganisations(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) state: String?,
        @RequestParam(required = true) countryCode: String?
    ): Resources<OrganisationAccountResource> {
        val organisations = searchOrganisations(organisationName = query, state = state, countryCode = countryCode);

        return Resources(
            organisations.map { account ->
                OrganisationAccountResource(
                    name = account.organisation.name,
                    type = account.type.toString(),
                    contractIds = account.contractIds.map { it.value },
                    accessExpiresOn = account.accessExpiresOn,
                    organisation = OrganisationResource(
                        name = account.organisation.name,
                        state = account.organisation.state?.let {
                            StateResource(
                                name = it.name,
                                id = it.id
                            )
                        },
                        country = account.organisation.country?.let {
                            CountryResource(
                                name = it.name,
                                id = it.id,
                                states = null
                            )
                        }

                    )
                )

            })
    }
}
