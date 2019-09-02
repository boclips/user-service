package com.boclips.users.presentation.resources

import org.springframework.hateoas.core.Relation

@Relation(collectionRelation = "countries")
data class CountryResource (
    val id: String,
    val name: String
)