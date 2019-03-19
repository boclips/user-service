package com.boclips.users.presentation.requests

import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min

open class CreateUserRequest(
    @Min(1)
    @Max(value = 200)
    val firstName: String,

    @Min(1)
    @Max(value = 200)
    val lastName: String,

    @Email
    @Max(value = 200)
    val email: String,

    @Min(1)
    @Max(value = 50)
    val password: String,

    @Max(value = 50)
    val mixPanelId: String?,

    @Max(value = 100)
    val subjects: String?,

    @Max(value = 50)
    val referralCode: String?
)