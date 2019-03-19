package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountRepository
import org.springframework.stereotype.Component

@Component
class MongoAccountRepository(
    private val userDocumentMongoRepository: UserDocumentMongoRepository
) : AccountRepository {
    override fun markAsReferred(id: AccountId): Account? {
        return userDocumentMongoRepository
            .findById(id.value)
            .map { it.copy(isReferral = true) }
            .map { save(it.toAccount()) }
            .orElse(null)
    }

    override fun activate(id: AccountId): Account? = userDocumentMongoRepository
        .findById(id.value)
        .map { it.copy(activated = true) }
        .map { save(it.toAccount()) }
        .orElse(null)

    override fun findAll(ids: List<AccountId>) = userDocumentMongoRepository
        .findAllById(ids.map { it.value })
        .mapNotNull { it.toAccount() }

    override fun save(account: Account) = userDocumentMongoRepository
        .save(UserDocument.from(account))
        .toAccount()

    override fun findById(id: AccountId): Account? {
        return userDocumentMongoRepository
            .findById(id.value)
            .orElse(null)
            ?.toAccount()
    }
}

