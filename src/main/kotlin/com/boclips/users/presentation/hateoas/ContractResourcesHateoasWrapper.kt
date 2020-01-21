package com.boclips.users.presentation.hateoas

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link

open class ContractResourcesHateoasWrapper(
    val _embedded: ContractResourcesWrapper,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val _links: Map<String, Link> = emptyMap()
)
