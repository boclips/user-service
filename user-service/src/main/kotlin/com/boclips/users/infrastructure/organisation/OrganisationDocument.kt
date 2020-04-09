package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.OrganisationType
import com.mongodb.DBRef
import org.bson.types.ObjectId
import java.time.Instant

data class OrganisationDocument(
    val _id: ObjectId?,
    val name: String,
    val role: String?,
    val domain: String?,
    val externalId: String?,
    val type: OrganisationType,
    val dealType: DealType?,
    val country: LocationDocument?,
    val state: LocationDocument?,
    val postcode: String?,
    val allowsOverridingUserIds: Boolean?,
    val parentOrganisation: DBRef? = null,
    val parent: OrganisationDocument? = null,
    val accessExpiresOn: Instant? = null,
    val contentPackageId: String? = null
)

data class LocationDocument(
    val code: String
)
