package com.boclips.users.domain.model.referrals

data class NewReferral(
    val referralCode: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val externalIdentifier: String,
    val status: String
)
