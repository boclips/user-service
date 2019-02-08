package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.users.User
import com.boclips.users.domain.model.users.UserRepository
import org.springframework.stereotype.Component

@Component
class MongoUserRepository(
    private val userDocumentMongoRepository: UserDocumentMongoRepository
) : UserRepository {

    override fun save(user: User) = userDocumentMongoRepository
        .save(UserDocument.from(user))
        .toUser()

    override fun findById(id: String) = userDocumentMongoRepository
        .findById(id)
        .orElse(null)
        ?.toUser()
}

