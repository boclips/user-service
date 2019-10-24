package com.boclips.users.presentation.resources.school

import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "countries")
data class CountryResource(
        val id: String,
        val name: String,
        val states: List<EntityModel<StateResource>>?
)
