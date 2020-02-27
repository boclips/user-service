package com.boclips.users.application.commands

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.accessrules.AccessRule
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.service.AccessRuleRepository
import com.boclips.users.domain.service.AccountRepository
import org.springframework.stereotype.Service

@Service
class GetAccessRulesOfUser(
    private val accountRepository: AccountRepository,
    private val accessRuleRepository: AccessRuleRepository,
    private val getOrImportUser: GetOrImportUser
) {
    operator fun invoke(userId: UserId): List<AccessRule> {
        return findUser(userId)
            ?.accessRuleIds?.mapNotNull(accessRuleRepository::findById)
            ?: emptyList()
    }

    private fun findUser(userId: UserId): Organisation<*>? {
        return getOrImportUser(userId).organisationAccountId?.let {
            accountRepository.findAccountById((it))
        }
    }
}
