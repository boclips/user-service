package com.boclips.users.presentation.resources.school

import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "states")
data class StateResource(
    val id: String,
    val name: String
)
