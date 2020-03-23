package com.boclips.users.presentation.requests

class CreateContentPackageRequest(
    var name: String,
    var accessRuleIds: List<String>
)