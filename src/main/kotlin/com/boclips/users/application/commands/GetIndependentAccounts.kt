package com.boclips.users.application.commands

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.service.AccountRepository
import com.boclips.users.infrastructure.organisation.AccountSearchRequest
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class GetIndependentAccounts(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(request : AccountSearchRequest): Page<Account<*>> {
        if (request.countryCode.isNullOrBlank()) {
            throw RuntimeException("You must provide a country code")
        }

        return accountRepository.findIndependentSchoolsAndDistricts(request)!!
    }
}
