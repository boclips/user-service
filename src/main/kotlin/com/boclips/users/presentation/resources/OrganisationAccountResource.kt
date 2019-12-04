package com.boclips.users.presentation.resources

import org.springframework.hateoas.core.Relation
import java.time.ZonedDateTime

@Relation(collectionRelation = "organisationAccount")
data class OrganisationAccountResource(
    val id: String?,
    val name: String,
    val contractIds: List<String>,
    val accessExpiresOn: ZonedDateTime?,
    val type: String?,
    val organisation: OrganisationResource
)
