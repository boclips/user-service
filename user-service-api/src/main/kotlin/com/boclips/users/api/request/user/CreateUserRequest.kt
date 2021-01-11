package com.boclips.users.api.request.user

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = CreateUserRequest.CreateTeacherRequest::class)
sealed class CreateUserRequest(
) {

    @JsonTypeName("teacher")
    data class CreateTeacherRequest(
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

        @field:Size(max = 200, message = "utmSource cannot be longer than 200 characters")
        var utmSource: String? = null,

        @field:Size(max = 200, message = "utmMedium cannot be longer than 200 characters")
        var utmMedium: String? = null,

        @field:Size(max = 200, message = "utmCampaign cannot be longer than 200 characters")
        var utmCampaign: String? = null,

        @field:Size(max = 200, message = "utmTerm cannot be longer than 200 characters")
        var utmTerm: String? = null,

        @field:Size(max = 200, message = "utmContent cannot be longer than 200 characters")
        var utmContent: String? = null,

        @field:Size(max = 50, message = "Referral code cannot be longer than 50 characters")
        var referralCode: String? = null,

        @field:NotNull(message = "recaptchaToken is required")
        @field:NotEmpty(message = "recaptchaToken must be set")
        var recaptchaToken: String? = null
    ) : CreateUserRequest()

    @JsonTypeName("apiUser")
    data class CreateApiUserRequest(
        val apiUserId: String,
        val organisationId: String
    ) : CreateUserRequest()
}
