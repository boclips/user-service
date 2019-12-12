package com.boclips.users.infrastructure.organisation

data class AccountSearchRequest(
    val countryCode: String,
    val page: Int?,
    val size: Int?
)
