package com.boclips.users.api.response.accessrule

import org.springframework.hateoas.Link

open class ContentPackageResource(
    val id: String,
    val name: String,
    val accessRules: List<AccessRuleResource>,
    val _links: Map<String, Link>
)
