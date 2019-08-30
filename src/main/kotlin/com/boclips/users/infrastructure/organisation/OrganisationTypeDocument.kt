package com.boclips.users.infrastructure.organisation

data class OrganisationTypeDocument(val id: String?, val type: String)  {
    companion object {
        const val TYPE_NO_ORGANISATION = "NO_ORGANISATION"
        const val TYPE_API = "API"
        const val TYPE_DISTRICT = "DISTRICT"
    }
}