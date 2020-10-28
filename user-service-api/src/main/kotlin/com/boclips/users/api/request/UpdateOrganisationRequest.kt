package com.boclips.users.api.request

class UpdateOrganisationRequest(
    var domain: String? = null,
    var contentPackageId : String? = null,
    var billing : Boolean? = null,
    var accessExpiresOn: String? = null,
    var features: Map<String, Boolean>? = null
)
