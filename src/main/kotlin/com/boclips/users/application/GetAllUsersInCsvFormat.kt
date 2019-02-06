package com.boclips.users.application

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.infrastructure.user.UserDocumentMongoRepository
import com.boclips.users.presentation.users.UserResource
import com.boclips.users.presentation.users.UserToResourceConverter
import org.springframework.stereotype.Component

@Component
class GetAllUsersInCsvFormat(
        private val identityProvider: IdentityProvider
) {
    operator fun invoke(): List<String> {
        val users = identityProvider.getUsers().map { it.toUser() }
        val userResources = UserToResourceConverter.convert(users)
        return userResources.map {
            it.id.plus(",").plus(it.activated.toString())
        }

    }
}