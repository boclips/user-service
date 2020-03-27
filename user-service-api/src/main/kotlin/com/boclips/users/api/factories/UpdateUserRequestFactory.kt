package com.boclips.users.api.factories

import com.boclips.users.api.request.user.MarketingTrackingRequest
import com.boclips.users.api.request.user.UpdateUserRequest

class UpdateUserRequestFactory {
    companion object {
        fun sample(
            firstName: String? = "Hans",
            lastName: String? = "Zimmer",
            subjects: List<String>? = emptyList(),
            ages: List<Int>? = listOf(1, 2, 4),
            hasOptedIntoMarketing: Boolean? = false,
            referralCode: String? = "ABCD",
            utm: MarketingTrackingRequest? = MarketingTrackingRequest(
                source = "",
                campaign = "",
                content = "",
                medium = "",
                term = ""
            ),
            schoolName: String? = "Sunnydale High School",
            schoolId: String? = null,
            state: String? = "CA",
            country: String? = "USA",
            role: String? = "TEACHER"
        ): UpdateUserRequest {
            return UpdateUserRequest(
                firstName = firstName,
                lastName = lastName,
                subjects = subjects,
                ages = ages,
                hasOptedIntoMarketing = hasOptedIntoMarketing,
                referralCode = referralCode,
                utm = utm,
                schoolName = schoolName,
                schoolId = schoolId,
                state = state,
                country = country,
                role = role
            )
        }
    }
}
