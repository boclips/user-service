package com.boclips.users.presentation.requests

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

open class CreateUserRequest(
    @field:Size(min = 1, max = 200, message = "First name must be between 1 and 200 characters")
    @field:NotNull(message = "First name is required")
    var firstName: String? = null,

    @field:Size(min = 1, max = 200, message = "Last name must be between 1 and 200 characters")
    @field:NotNull(message = "Last name is required")
    var lastName: String? = null,

    @field:Email(message = "Email must be valid")
    @field:NotNull(message = "Email is required")
    @field:NotEmpty(message = "Email must be set")
    var email: String? = null,

    @field:Size(min = 8, max = 50, message = "Password length must be at least 8")
    @field:NotNull(message = "Password is required")
    @field:NotEmpty(message = "Password must be set")
    var password: String? = null,

    @field:Size(max = 100, message = "Analytics ID cannot be longer than 100 characters")
    var analyticsId: String? = null,

    @field:Size(max = 50, message = "Cannot have more than 50 subjects")
    var subjects: List<String>? = null,

    @field:Size(max = 19, message = "The age range cannot have more than 19 years")
    var ageRange: List<Int>? = null,

    @field:Size(max = 50, message = "Referral code cannot be longer than 50 characters")
    var referralCode: String? = null,

    @field:NotNull(message = "recaptchaToken is required")
    @field:NotEmpty(message = "recaptchaToken must be set")
    var recaptchaToken: String? = null,

    @field:NotNull(message = "Marketing preferences must not be null")
    var hasOptedIntoMarketing: Boolean? = null
)