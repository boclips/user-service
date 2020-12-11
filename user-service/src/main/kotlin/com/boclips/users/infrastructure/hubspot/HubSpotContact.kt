package com.boclips.users.infrastructure.hubspot

data class HubSpotContact(
    val email: String,
    val properties: List<HubSpotProperty>
)

data class RetrievedHubSpotContact(
    val vid: String
)

data class HubSpotProperty(
    val property: String,
    val value: String
)
