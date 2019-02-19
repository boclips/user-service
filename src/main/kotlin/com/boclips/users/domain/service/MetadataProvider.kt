package com.boclips.users.domain.service

import com.boclips.users.domain.model.AccountMetadata
import com.boclips.users.domain.model.identity.IdentityId

interface MetadataProvider {
    fun getMetadata(id: IdentityId): AccountMetadata
}