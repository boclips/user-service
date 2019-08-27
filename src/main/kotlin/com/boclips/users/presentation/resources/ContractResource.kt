package com.boclips.users.presentation.resources

import org.springframework.hateoas.core.Relation

@Relation(collectionRelation = "contracts")
data class ContractResource(val id: String)