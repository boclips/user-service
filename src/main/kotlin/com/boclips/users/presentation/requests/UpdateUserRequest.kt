package com.boclips.users.presentation.requests

import javax.validation.Valid
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

    var hasOptedIntoMarketing: Boolean? = null,

    @field:Size(max = 50, message = "Referral code cannot be longer than 50 characters")
    var referralCode: String? = null,

    @field:Valid
    var marketingTrackingRequest: MarketingTrackingRequest? = null
)

class MarketingTrackingRequest(
    @field:Size(max = 200, message = "utmSource cannot be longer than 200 characters")
    var utmSource: String? = null,

    @field:Size(max = 200, message = "utmMedium cannot be longer than 200 characters")
    var utmMedium: String? = null,

    @field:Size(max = 200, message = "utmCampaign cannot be longer than 200 characters")
    var utmCampaign: String? = null,

    @field:Size(max = 200, message = "utmTerm cannot be longer than 200 characters")
    var utmTerm: String? = null,

    @field:Size(max = 200, message = "utmContent cannot be longer than 200 characters")
    var utmContent: String? = null
)
