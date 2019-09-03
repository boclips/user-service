package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor.currentUserHasRole
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
        if (!currentUserHasRole(UserRoles.VIEW_CONTRACTS)) {
            throw PermissionDeniedException()
        }

        return findOrganisation(user)?.contractIds?.mapNotNull(contractRepository::findById) ?: emptyList()
    }

    private fun findOrganisation(user: User): Organisation? {
        return user.organisationId?.let {
            organisationRepository.findById(it)
        }
    }
}