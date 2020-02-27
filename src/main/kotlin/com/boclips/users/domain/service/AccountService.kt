package com.boclips.users.domain.service

import com.boclips.users.domain.model.account.District
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.model.account.School
import org.springframework.stereotype.Service

@Service
class AccountService(
    val americanSchoolsProvider: AmericanSchoolsProvider,
    val accountRepository: AccountRepository
) {
    fun findOrCreateSchooldiggerSchool(externalSchoolId: String): Organisation<School>? {
        var schoolAccount = accountRepository.findAccountByExternalId(externalSchoolId)
            ?.takeIf { it.organisation is School }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as Organisation<School>
            }

        if (schoolAccount == null) {
            val (school, district) = americanSchoolsProvider.fetchSchool(externalSchoolId) ?: null to null
            schoolAccount = school
                ?.copy(district = district?.let { getOrCreateDistrict(district) })
                ?.let { accountRepository.save(it) }
        }

        return schoolAccount
    }

    private fun getOrCreateDistrict(district: District): Organisation<District>? {
        return accountRepository.findAccountByExternalId(district.externalId)
            ?.takeIf { it.organisation is District }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as Organisation<District>
            }
            ?: accountRepository.save(district)
    }
}
