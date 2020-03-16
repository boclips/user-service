package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserUpdateCommand
import org.springframework.stereotype.Component

@Component
class AssignUsersByOrganisationDomain(
    private val organisationRepository: OrganisationRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(id: String): List<User> {
        val organisation =
            organisationRepository.findOrganisationById(OrganisationId(id)) ?: throw OrganisationNotFoundException(id)
        val domain = organisation.details.domain

        val orphanUsers = domain?.let { userRepository.findOrphans(it, organisation.id) } ?: emptyList()

        return orphanUsers.map { userRepository.update(it, UserUpdateCommand.ReplaceOrganisationId(organisation.id)) }
    }
}
