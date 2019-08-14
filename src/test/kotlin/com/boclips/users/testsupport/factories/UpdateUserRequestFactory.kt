package com.boclips.users.testsupport.factories

import com.boclips.users.presentation.requests.UpdateUserRequest

class UpdateUserRequestFactory {
    companion object {
        fun sample(
            name: String? = "Hans",
            subjects: List<String>? = listOf("argriculture"),
            ages: List<Int>? = listOf(1, 2, 4),
            hasOptedIntoMarketing: Boolean? = false
        ): UpdateUserRequest {
            return UpdateUserRequest(
                name = name,
                subjects = subjects,
                ages = ages,
                hasOptedIntoMarketing = hasOptedIntoMarketing
            )
        }
    }
}
