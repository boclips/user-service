package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.UserRepository
import org.springframework.stereotype.Component

@Component
class MongoUserRepository(
    private val userDocumentMongoRepository: UserDocumentMongoRepository,
    private val userDocumentConverter: UserDocumentConverter
) : UserRepository {
    override fun activate(id: UserId): User? = userDocumentMongoRepository
        .findById(id.value)
        .map { it.copy(activated = true) }
        .map { save(userDocumentConverter.convertToUser(it)) }
        .orElse(null)

    override fun findAll(ids: List<UserId>) = userDocumentMongoRepository
        .findAllById(ids.map { it.value })
        .mapNotNull { userDocumentConverter.convertToUser(it) }

    override fun findAll(): List<User> {
        return userDocumentMongoRepository.findAll().map { document -> userDocumentConverter.convertToUser(document) }
    }

    override fun findById(id: UserId): User? {
        return userDocumentMongoRepository
            .findById(id.value)
            .orElse(null)
            ?.let { userDocumentConverter.convertToUser(it) }
    }

    override fun save(user: User) = userDocumentConverter.convertToUser(
        userDocumentMongoRepository
            .save(UserDocument.from(user))
    )

    override fun count(): Int {
        return userDocumentMongoRepository.count().toInt()
    }
}

