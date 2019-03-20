package com.boclips.users.presentation.requests

import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

open class CreateUserRequest(
    @Min(1, message = "First name cannot be shorter than 1 character.")
    @Max(value = 200, message = "First name cannot be longer than 200 characters")
    @NotNull(message = "First name is required")
    var firstName: String,

    @Min(1, message = "Last name cannot be shorter than 1 character.")
    @Max(value = 200, message = "Last name cannot be longer than 200 characters.")
    @NotNull(message = "Last name is required.")
    var lastName: String,

    @Email
    @Max(value = 200)
    @NotNull(message = "Email is required.")
    var email: String,

    @Min(1)
    @Max(value = 50)
    @NotNull(message = "Password is required.")
    var password: String,

    @Max(value = 50, message = "Analytics ID cannot be longer than 50 characters")
    var analyticsId: String?,

    @Max(value = 100, message = "Subjects cannot be longer than 100 characters")
    var subjects: String?,

    @Max(value = 50, message = "Referral code cannot be longer than 50 characters")
    var referralCode: String?
)