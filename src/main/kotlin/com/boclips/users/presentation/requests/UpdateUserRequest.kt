package com.boclips.users.presentation.requests

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

open class UpdateUserRequest(
    @field:Size(min = 1, max = 400, message = "Name must be between 1 and 400 characters")
    @field:NotNull(message = "Name is required")
    var name: String? = null,

    @field:Size(min = 1, max = 50, message = "Cannot have less than 1 or more than 50 subjects")
    @field:NotNull(message = "Subjects are required")
    var subjects: List<String>? = null,

    @field:Size(min = 1, max = 19, message = "Cannot have less than 1 or more than 99 ages")
    @field:NotNull(message = "Ages are required")
    var ages: List<Int>? = null,

    @field:NotNull(message = "Marketing preferences must not be null")
    var hasOptedIntoMarketing: Boolean? = null
)
