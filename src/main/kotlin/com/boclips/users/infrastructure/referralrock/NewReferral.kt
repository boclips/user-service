package com.boclips.users.infrastructure.referralrock

data class NewReferral(
    val referralCode: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val externalIdentifier: String
)