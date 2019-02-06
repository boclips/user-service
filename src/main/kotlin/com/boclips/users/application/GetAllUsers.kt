package com.boclips.users.application

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.infrastructure.user.UserDocumentMongoRepository
import com.boclips.users.presentation.users.UserResource
import com.boclips.users.presentation.users.UserToResourceConverter
import org.springframework.stereotype.Component

@Component
class GetAllUsers(
        private val identityProvider: IdentityProvider
) {
    operator fun invoke(): List<UserResource> {
        val users = identityProvider.getUsers().map { it.toUser() }
        return UserToResourceConverter.convert(users)
    }
}