package com.boclips.users.api.request

import javax.validation.constraints.NotEmpty

class UpdateAccessRuleRequest {
    @field:NotEmpty
    var id: String? = null
    var name: String? = null
    var collectionIds: List<String>? = null
    var videoIds: List<String>? = null
    var videoTypes: List<String>? = null
    var channelIds: List<String>? = null
}