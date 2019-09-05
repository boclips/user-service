package com.boclips.users.presentation.resources.school

import org.springframework.hateoas.core.Relation

@Relation(collectionRelation = "schools")
data class SchoolResource(
    val id: String,
    val name: String
)