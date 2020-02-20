package com.boclips.users.presentation.requests

import javax.validation.constraints.NotEmpty

class CreateAccountRequest {
    @field:NotEmpty
    var name: String? = null
    @field:NotEmpty
    var role: String? = null
    var accessRuleIds: List<String>? = null
}
