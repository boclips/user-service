package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountRepository
import org.springframework.stereotype.Component

@Component
class MongoAccountRepository(
    private val userDocumentMongoRepository: UserDocumentMongoRepository
) : AccountRepository {

    override fun save(account: Account) = userDocumentMongoRepository
        .save(UserDocument.from(account))
        .toUser()

    override fun findById(id: String) = userDocumentMongoRepository
        .findById(id)
        .orElse(null)
        ?.toUser()
}

