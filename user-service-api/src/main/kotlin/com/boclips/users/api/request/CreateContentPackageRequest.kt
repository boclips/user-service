package com.boclips.users.api.request

class CreateContentPackageRequest(
    var name: String,
    var accessRules: List<AccessRuleRequest>
)
