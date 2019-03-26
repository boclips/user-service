package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.UserRepository
import org.springframework.stereotype.Component

@Component
class MongoUserRepository(
    private val userDocumentMongoRepository: UserDocumentMongoRepository
) : UserRepository {
    override fun activate(id: UserId): User? = userDocumentMongoRepository
        .findById(id.value)
        .map { it.copy(activated = true) }
        .map { save(it.toUser()) }
        .orElse(null)

    override fun findAll(ids: List<UserId>) = userDocumentMongoRepository
        .findAllById(ids.map { it.value })
        .mapNotNull { it.toUser() }

    override fun findAll(): List<User> {
        return userDocumentMongoRepository.findAll().map { document -> document.toUser() }
    }

    override fun findById(id: UserId): User? {
        return userDocumentMongoRepository
            .findById(id.value)
            .orElse(null)
            ?.toUser()
    }

    override fun save(user: User) = userDocumentMongoRepository
        .save(UserDocument.from(user))
        .toUser()

    override fun count(): Int {
        return userDocumentMongoRepository.count().toInt()
    }
}

