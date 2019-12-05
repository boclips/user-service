package com.boclips.users.presentation.resources

import org.springframework.hateoas.core.Relation
import java.time.ZonedDateTime

@Relation(collectionRelation = "organisationAccount")
data class OrganisationAccountResource(
    val id: String?,
    val contractIds: List<String>,
    val accessExpiresOn: ZonedDateTime?,
    val organisation: OrganisationResource
)
