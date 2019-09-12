package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.School
import org.springframework.stereotype.Service

@Service
class OrganisationService(
    val americanSchoolsProvider: AmericanSchoolsProvider,
    val organisationAccountRepository: OrganisationAccountRepository
) {
    fun findOrCreateAmericanSchool(externalSchoolId: String): OrganisationAccount<School> {
        organisationAccountRepository.findOrganisationAccountByExternalId(externalSchoolId)
        TODO()
    }
}