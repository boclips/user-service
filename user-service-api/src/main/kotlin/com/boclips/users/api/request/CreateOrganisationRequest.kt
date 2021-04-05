package com.boclips.users.api.request

import javax.validation.constraints.NotEmpty

class CreateOrganisationRequest {
    @field:NotEmpty
    var name: String? = null

    @field:NotEmpty
    var role: String? = null
    var tags: Set<String>? = null
    var contentPackageId: String? = null
}
