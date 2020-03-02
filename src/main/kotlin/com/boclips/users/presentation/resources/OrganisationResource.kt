package com.boclips.users.presentation.resources

import org.springframework.hateoas.server.core.Relation
import java.time.ZonedDateTime

@Relation(collectionRelation = "organisations")
data class OrganisationResource(
    val id: String?,
    val accessRuleIds: List<String>,
    val accessExpiresOn: ZonedDateTime?,
    val organisationDetails: OrganisationDetailsResource
)
