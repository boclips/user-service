package com.boclips.users.presentation.resources

import org.springframework.hateoas.server.core.Relation
import java.time.ZonedDateTime

@Relation(collectionRelation = "account")
data class AccountResource(
    val id: String?,
    val contractIds: List<String>,
    val accessExpiresOn: ZonedDateTime?,
    val organisation: OrganisationResource
)
