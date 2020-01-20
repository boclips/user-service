package com.boclips.users.application.commands

import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.service.AccountRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class GetIndependentAccounts(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(filter: OrganisationFilter): Page<Account<*>> {
        return accountRepository.findAccounts(
            filter.countryCode,
            filter.organisationTypes,
            filter.page,
            filter.size
        )!!
    }
}
