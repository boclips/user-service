package com.boclips.users.application

import com.boclips.security.utils.UserExtractor.getIfHasRole
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.contract.SelectedContentContract
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.SelectedContentContractRepository
import org.springframework.stereotype.Service

@Service
class GetContracts(
    private val organisationRepository: OrganisationRepository,
    private val selectedContentContractRepository: SelectedContentContractRepository
) {
    operator fun invoke(user: User): List<SelectedContentContract> {
        return getIfHasRole(UserRoles.VIEW_CONTRACTS) {
            user.account.platform.getIdentifier()?.let { organisationId ->
                organisationRepository.findById(organisationId)?.contractIds?.mapNotNull { contractId ->
                    selectedContentContractRepository.findById(contractId)
                } ?: emptyList()
            } ?: emptyList()
        } ?: throw PermissionDeniedException()
    }
}