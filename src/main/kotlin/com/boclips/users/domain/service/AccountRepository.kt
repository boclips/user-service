package com.boclips.users.domain.service

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.District
import com.boclips.users.domain.model.account.School
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.infrastructure.organisation.AccountSearchRequest
import org.springframework.data.domain.Page
import java.time.ZonedDateTime
import java.util.Collections.emptyList

interface AccountRepository {
    fun save(
        apiIntegration: ApiIntegration,
        contractIds: List<ContractId> = emptyList(),
        role: String? = null
    ): Account<ApiIntegration>

    fun save(
        school: School,
        accessExpiresOn: ZonedDateTime? = null
    ): Account<School>

    fun save(
        district: District,
        accessExpiresOn: ZonedDateTime? = null
    ): Account<District>

    fun update(update: AccountUpdate): Account<*>?

    fun findAccounts(searchRequest: AccountSearchRequest): Page<Account<*>>
    fun findAccountsByParentId(parentId: AccountId): List<Account<*>>
    fun findAccountById(id: AccountId): Account<*>?
    fun findAccountByExternalId(id: String): Account<*>?
    fun findIndependentSchoolsAndDistricts(searchRequest: AccountSearchRequest): Page<Account<*>>?

    fun findApiIntegrationByRole(role: String): Account<ApiIntegration>?
    fun findApiIntegrationByName(name: String): Account<ApiIntegration>?

    fun lookupSchools(schoolName: String, countryCode: String) : List<LookupEntry>
    fun findSchoolById(id: AccountId): Account<School>?
    fun findSchools(): List<Account<School>>
}
