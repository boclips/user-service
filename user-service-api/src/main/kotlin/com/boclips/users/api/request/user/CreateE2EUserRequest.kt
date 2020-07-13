package com.boclips.users.api.request.user

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

open class CreateE2EUserRequest(
    @field:Email(message = "Email must be valid")
    @field:NotNull(message = "Email is required")
    @field:NotEmpty(message = "Email must be set")
    var email: String? = null,

    @field:Size(min = 8, max = 50, message = "Password length must be at least 8")
    @field:NotNull(message = "Password is required")
    @field:NotEmpty(message = "Password must be set")
    var password: String? = null,

    @field:NotEmpty(message = "Organisation must be set")
    var organisationId: String? = null
)
