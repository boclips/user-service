package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor.getIfHasRole
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.ContractRepository
import com.boclips.users.domain.service.OrganisationRepository
import org.springframework.stereotype.Service

@Service
class GetContracts(
    private val organisationRepository: OrganisationRepository,
    private val contractRepository: ContractRepository
) {
    operator fun invoke(user: User): List<Contract> {
        return getIfHasRole(UserRoles.VIEW_CONTRACTS) {
            findOrganisation(user)?.contractIds?.mapNotNull(contractRepository::findById)
        } ?: throw PermissionDeniedException()
    }

    private fun findOrganisation(user: User): Organisation? {
        return user.organisationId?.let {
            organisationRepository.findById(it)
        }
    }
}