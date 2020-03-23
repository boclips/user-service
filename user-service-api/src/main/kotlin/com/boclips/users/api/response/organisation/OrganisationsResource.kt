package com.boclips.users.api.response.organisation

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link
import org.springframework.hateoas.PagedModel

open class OrganisationsResource(
    val _embedded: OrganisationsWrapper,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var page: PagedModel.PageMetadata? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val _links: Map<String, Link>?
)

data class OrganisationsWrapper(val organisations: List<OrganisationResource>)
