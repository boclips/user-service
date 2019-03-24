package com.boclips.users.domain.service

import com.boclips.users.domain.model.AccountMetadata
import com.boclips.users.domain.model.UserId

interface MetadataProvider {
    fun getMetadata(id: UserId): AccountMetadata
    fun getAllMetadata(ids: List<UserId>): Map<UserId, AccountMetadata>
}