package com.boclips.users.infrastructure.referralrock

data class ReferralActionResponse(
    val message: String,
    val referral: ReferralResponse
)

data class ReferralResponse(
    val id: String,
    val status: String
)
