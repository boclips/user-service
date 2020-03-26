package com.boclips.users.api.response.school

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link

open class SchoolsResource(
    val _embedded: SchoolsWrapperResource,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val _links: Map<String, Link>?
)

data class SchoolsWrapperResource(val schools: List<SchoolResource>)
