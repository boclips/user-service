package com.boclips.users.api.request

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import javax.validation.constraints.NotEmpty

class UpdateContentPackageRequest (
    @field:NotEmpty
    var name: String? = null,
    var id: String? = null,
    @JsonSetter(contentNulls = Nulls.FAIL)
    var accessRules: Set<AccessRuleRequest>? = null
)