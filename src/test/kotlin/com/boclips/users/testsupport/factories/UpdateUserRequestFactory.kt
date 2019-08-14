package com.boclips.users.testsupport.factories

import com.boclips.users.presentation.requests.UpdateUserRequest

class UpdateUserRequestFactory {
    companion object {
        fun sample(
            firstName: String? = "Hans",
            lastName: String? = "Zimmer",
            subjects: List<String>? = listOf("argriculture"),
            ages: List<Int>? = listOf(1, 2, 4),
            hasOptedIntoMarketing: Boolean? = false
        ): UpdateUserRequest {
            return UpdateUserRequest(
                firstName = firstName,
                lastName = lastName,
                subjects = subjects,
                ages = ages,
                hasOptedIntoMarketing = hasOptedIntoMarketing
            )
        }
    }
}