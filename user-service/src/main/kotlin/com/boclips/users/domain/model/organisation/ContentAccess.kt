package com.boclips.users.domain.model.organisation

import com.boclips.security.utils.Client
import com.boclips.users.domain.model.access.ContentPackageId

sealed class ContentAccess {

    class SimpleAccess(val id: ContentPackageId) : ContentAccess()
    class ClientBasedAccess(val clientAccess: Map<Client, ContentPackageId>) : ContentAccess()

    override fun equals(other: Any?): Boolean {
        return if (this is SimpleAccess && other is SimpleAccess) {
            return this.id == other.id
        } else false
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
