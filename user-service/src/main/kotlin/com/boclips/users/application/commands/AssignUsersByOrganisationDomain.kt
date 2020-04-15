package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserUpdate
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class AssignUsersByOrganisationDomain(
    private val organisationRepository: OrganisationRepository,
    private val userRepository: UserRepository
) {
    companion object : KLogging()

    operator fun invoke(id: String): List<User> {
        val organisation: Organisation<*> =
            organisationRepository.findOrganisationById(OrganisationId(id)) ?: throw OrganisationNotFoundException(id)

        val orphanUsers = when (organisation.details) {
            is School -> handleFlatOrganisation(organisation)
            is District -> handleHierarchicalOrganisation(organisation)
            is ApiIntegration -> handleFlatOrganisation(organisation)
        }

        logger.info { "Identified ${orphanUsers.size} users not associate to organisation ${organisation.id} with domain ${organisation.details.domain}" }

        return orphanUsers.map { userRepository.update(it, UserUpdate.ReplaceOrganisation(organisation)) }
    }

    private fun handleFlatOrganisation(organisation: Organisation<*>): List<User> {
        val domain = organisation.details.domain
        return domain?.let { userRepository.findOrphans(it, organisation.id) } ?: emptyList()
    }

    private fun handleHierarchicalOrganisation(organisation: Organisation<*>): List<User> {
        val childOrganisations = organisationRepository.findOrganisationsByParentId(organisation.id)

        val domain = organisation.details.domain
        val orphanUsers = domain?.let { userRepository.findOrphans(it, organisation.id) } ?: emptyList()

        return orphanUsers.filter { user ->
            !childOrganisations.map { it.id }.contains(user.organisationId)
        }
    }
}
