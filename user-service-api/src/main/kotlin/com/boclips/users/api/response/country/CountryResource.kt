package com.boclips.users.api.response.country

import com.boclips.users.api.response.state.StateResource
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link

data class CountryResource(
    val id: String,
    val name: String,
    val states: List<StateResource>?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val _links: Map<String, Link>?
)
