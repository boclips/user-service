package com.boclips.users.presentation.resources

import com.boclips.users.api.response.accessrule.AccessRuleResource
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "content-package")
open class ContentPackageResource(
    val id: String,
    val name: String,
    val accessRules: List<AccessRuleResource>,
    val _links: Map<String, Link>
)
