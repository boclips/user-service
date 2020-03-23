package com.boclips.users.api.response.accessrule

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.hateoas.Link

open class AccessRulesResource(
    val _embedded: AccessRulesWrapper,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val _links: Map<String, Link> = emptyMap()
)

data class AccessRulesWrapper(val accessRules: List<AccessRuleResource>)
