package com.boclips.users.presentation.resources.school

import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "schools")
data class SchoolResource(
    val id: String,
    val name: String
)
