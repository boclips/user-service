package com.boclips.users.api.response.accessrule

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link

open class ContentPackageResource(
    val id: String,
    val name: String,
    val accessRules: List<AccessRuleResource>,
    val _links: Map<String, Link>
)

open class ContentPackagesResource(
    val _embedded: ContentPackagesWrapperResource,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val _links: Map<String, Link>?
)

data class ContentPackagesWrapperResource(
    val contentPackages: List<ContentPackageResource>
)
