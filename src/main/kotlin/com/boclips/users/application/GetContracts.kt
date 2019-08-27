package com.boclips.users.application

import com.boclips.security.utils.UserExtractor.getIfHasRole
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.service.OrganisationRepository
import org.springframework.stereotype.Service

@Service
class GetContracts(private val organisationRepository: OrganisationRepository) {
    operator fun invoke(user: User): List<ContractId> {
        return getIfHasRole(UserRoles.VIEW_CONTRACTS) {
            user.account.platform.getIdentifier()?.let {
                organisationRepository.findById(it)?.contractIds ?: emptyList()
            } ?: emptyList()
        } ?: throw PermissionDeniedException()
    }
}