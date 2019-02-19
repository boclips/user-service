package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountRepository
import org.springframework.stereotype.Component

@Component
class MongoAccountRepository(
    private val userDocumentMongoRepository: UserDocumentMongoRepository
) : AccountRepository {
    override fun findAll(ids: List<String>) = userDocumentMongoRepository
        .findAllById(ids)
        .mapNotNull { it.toUser() }

    override fun save(account: Account) = userDocumentMongoRepository
        .save(UserDocument.from(account))
        .toUser()

    override fun findById(id: String) = userDocumentMongoRepository
        .findById(id)
        .orElse(null)
        ?.toUser()
}

