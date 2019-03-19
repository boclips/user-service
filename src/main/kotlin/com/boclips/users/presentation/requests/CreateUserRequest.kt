package com.boclips.users.presentation.requests

import com.boclips.users.domain.model.analytics.AnalyticsId

class CreateUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val mixPanelId: String?,
    val subjects: String?,
    val referralCode: String?
)