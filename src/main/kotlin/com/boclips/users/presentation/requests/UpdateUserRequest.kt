package com.boclips.users.presentation.requests

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

open class UpdateUserRequest(
    @field:Size(min = 1, max = 200, message = "First name must be between 1 and 200 characters")
    var firstName: String? = null,

    @field:Size(min = 1, max = 200, message = "Last name must be between 1 and 200 characters")
    var lastName: String? = null,

    @field:Size(min = 1, max = 50, message = "Cannot have less than 1 or more than 50 subjects")
    var subjects: List<String>? = null,

    @field:Size(min = 1, max = 19, message = "Cannot have less than 1 or more than 99 ages")
    var ages: List<Int>? = null,

    var hasOptedIntoMarketing: Boolean? = null
)
