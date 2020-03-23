package com.boclips.users.presentation.requests

import javax.validation.constraints.NotEmpty

class CreateOrganisationRequest {
    @field:NotEmpty
    var name: String? = null

    @field:NotEmpty
    var role: String? = null
    var contentPackageId: String? = null
}
