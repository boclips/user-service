package com.boclips.users.api.response.country

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link

open class CountriesResource(
    val _embedded: CountriesWrapperResource,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val _links: Map<String, Link>?
)

data class CountriesWrapperResource(
    val countries: List<CountryResource>
)
