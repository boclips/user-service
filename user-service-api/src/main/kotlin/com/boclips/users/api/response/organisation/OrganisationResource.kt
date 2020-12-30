package com.boclips.users.api.response.organisation

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link
import java.time.ZonedDateTime

open class OrganisationResource(
    val id: String,
    val accessExpiresOn: ZonedDateTime?,
    val contentPackageId: String?,
    val billing: Boolean?,
    val organisationDetails: OrganisationDetailsResource,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val deal: DealResource?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var _links: Map<String, Link>?
)
