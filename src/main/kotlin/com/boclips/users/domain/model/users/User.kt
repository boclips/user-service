package com.boclips.users.domain.model.users

import java.util.*

data class User(
        val id: String,
        val activated: Boolean,
        val firstName: String? = null,
        val lastName: String? = null,
        val email: String? = null,
        val createdDate: Date? = null,
        val mixpanelDistinctId: String? = null,
        val subjects: List<String>? = null
)