package com.boclips.users.application

import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.MetadataProvider
import org.springframework.stereotype.Component

@Component
class MigrateIdentities(
    private val accountRepository: AccountRepository,
    private val identityProvider: IdentityProvider,
    private val metadataProvider: MetadataProvider
) {
    operator fun invoke() {
        val allUsers = identityProvider.getUsers()

        allUsers.forEach { identity ->
            val account = accountRepository.findById(id = identity.id)
            val metadata = metadataProvider.getMetadata(id = identity.id)

            val updatedAccount = account?.copy(
                subjects = metadata.subjects,
                firstName = identity.firstName,
                lastName = identity.lastName,
                email = identity.email,
                analyticsId = metadata.analyticsId,
                referralCode = account.referralCode?.let { it } ?: ""
            )!!

            accountRepository.save(updatedAccount)
        }
    }
}