package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State

data class Address(
    val country: Country? = null,
    val state: State? = null,
    val postcode: String? = null
)
