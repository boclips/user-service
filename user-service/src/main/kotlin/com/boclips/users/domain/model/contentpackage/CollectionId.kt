package com.boclips.users.domain.model.contentpackage

import com.boclips.users.domain.service.UniqueId

data class CollectionId(val value: String) {
    companion object {
        operator fun invoke(): CollectionId {
            return CollectionId(value = UniqueId())
        }
    }
}
