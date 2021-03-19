package com.boclips.users.infrastructure.hubspot

data class HubSpotContact(
    val email: String,
    val vid: String?,
    val properties: List<HubSpotProperty>
)

data class HubSpotProperty(
    val property: String,
    val value: String
)
