package com.boclips.users.testsupport.factories

import com.boclips.users.presentation.requests.MarketingTrackingRequest
import com.boclips.users.presentation.requests.UpdateUserRequest

class UpdateUserRequestFactory {
    companion object {
        fun sample(
            firstName: String? = "Hans",
            lastName: String? = "Zimmer",
            subjects: List<String>? = listOf("argriculture"),
            ages: List<Int>? = listOf(1, 2, 4),
            hasOptedIntoMarketing: Boolean? = false,
            referralCode: String? = "ABCD",
            utm: MarketingTrackingRequest? = MarketingTrackingRequest(
                source = "",
                campaign = "",
                content = "",
                medium = "",
                term = ""
            )
        ): UpdateUserRequest {
            return UpdateUserRequest(
                firstName = firstName,
                lastName = lastName,
                subjects = subjects,
                ages = ages,
                hasOptedIntoMarketing = hasOptedIntoMarketing,
                referralCode = referralCode,
                utm = utm
            )
        }
    }
}
