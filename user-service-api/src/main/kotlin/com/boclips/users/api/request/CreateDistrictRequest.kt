package com.boclips.users.api.request

import javax.validation.constraints.NotEmpty

class CreateDistrictRequest {
    @field:NotEmpty
    lateinit var name: String

    @field:NotEmpty
    lateinit var type: String

    var contentPackageId: String? = null
}
