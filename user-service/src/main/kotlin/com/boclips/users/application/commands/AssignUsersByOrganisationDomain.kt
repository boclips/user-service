package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.LtiDeployment
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.model.user.UserUpdate
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class AssignUsersByOrganisationDomain(
    private val organisationRepository: OrganisationRepository,
    private val userRepository: UserRepository
) {
    companion object : KLogging()

    operator fun invoke(id: String): List<User> {
        val organisation: Organisation =
            organisationRepository.findOrganisationById(OrganisationId(id)) ?: throw OrganisationNotFoundException(id)

        val orphanUsers = when (organisation) {
            is School -> handleFlatOrganisation(organisation)
            is District -> handleHierarchicalOrganisation(organisation)
            is ApiIntegration -> handleFlatOrganisation(organisation)
            is LtiDeployment -> emptyList()
        }

        logger.info { "Identified ${orphanUsers.size} users not associate to organisation ${organisation.id} with domain ${organisation.domain}" }

        return orphanUsers.map { userRepository.update(it, UserUpdate.ReplaceOrganisation(organisation)) }
    }

    private fun handleFlatOrganisation(organisation: Organisation): List<User> {
        val domain = organisation.domain
        return domain?.let { userRepository.findOrphans(it, organisation.id) } ?: emptyList()
    }

    private fun handleHierarchicalOrganisation(organisation: Organisation): List<User> {
        val childOrganisations = organisationRepository.findOrganisationsByParentId(organisation.id)

        val domain = organisation.domain
        val orphanUsers = domain?.let { userRepository.findOrphans(it, organisation.id) } ?: emptyList()

        return orphanUsers.filter { user ->
            !childOrganisations.map { it.id }.contains(user.organisation?.id)
        }
    }
}
