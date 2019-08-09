package com.boclips.users.infrastructure.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "organisations")
data class OrganisationDocument(
    @Id
    val id: String,
    val name: String
)
