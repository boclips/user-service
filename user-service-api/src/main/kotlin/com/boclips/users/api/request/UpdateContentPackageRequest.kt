package com.boclips.users.api.request

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls

class UpdateContentPackageRequest (
    var title: String? = null,
    @JsonSetter(contentNulls = Nulls.FAIL)
    var accessRules: Set<UpdateAccessRuleRequest>? = null
)