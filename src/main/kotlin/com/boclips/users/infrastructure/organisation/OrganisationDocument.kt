package com.boclips.users.infrastructure.organisation

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "organisations")
data class OrganisationDocument(
        @Id
        val id: String?,
        val name: String,
        val role: String?,
        val contractIds: List<String> = emptyList(),
        val externalId: String?,
        val type: OrganisationType,
        val country: LocationDocument?,
        val state: LocationDocument?,
        @DBRef
        val parentOrganisation: OrganisationDocument? = null
)

enum class OrganisationType {
    API, SCHOOL, DISTRICT
}

data class LocationDocument(
    val code: String
)