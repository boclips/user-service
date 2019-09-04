package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor.currentUserHasRole
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.contract.Contract
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.ContractRepository
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.UserRepository
import org.springframework.stereotype.Service

@Service
class GetContracts(
    private val organisationRepository: OrganisationRepository,
    private val contractRepository: ContractRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: UserId): List<Contract> {
        if (!currentUserHasRole(UserRoles.VIEW_CONTRACTS)) {
            throw PermissionDeniedException()
        }

        return findOrganisation(userId)?.contractIds?.mapNotNull(contractRepository::findById) ?: emptyList()
    }

    private fun findOrganisation(userId: UserId): Organisation? {
        return userRepository.findById(userId)?.organisationId?.let {
            organisationRepository.findById(it)
        }
    }
}