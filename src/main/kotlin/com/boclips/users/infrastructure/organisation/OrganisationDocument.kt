package com.boclips.users.infrastructure.organisation

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "organisations")
data class OrganisationDocument(
    @BsonId
    val id: ObjectId,
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