package com.boclips.users.api.response.state

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link

open class StatesResource(
    val _embedded: StatesWrapperResource,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val _links: Map<String, Link>?
)

data class StatesWrapperResource(val states: List<StateResource>)
