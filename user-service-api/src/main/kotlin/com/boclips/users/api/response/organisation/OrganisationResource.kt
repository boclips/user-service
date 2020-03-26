package com.boclips.users.api.response.organisation

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link
import java.time.ZonedDateTime

open class OrganisationResource(
    val id: String,
    val accessExpiresOn: ZonedDateTime?,
    val contentPackageId: String?,
    val organisationDetails: OrganisationDetailsResource,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var _links: Map<String, Link>?
)
