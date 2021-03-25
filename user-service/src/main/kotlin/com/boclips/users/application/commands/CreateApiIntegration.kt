package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateOrganisationRequest
import com.boclips.users.application.exceptions.OrganisationAlreadyExistsException
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.access.ContentPackageRepository
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.ContentAccess
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.organisation.OrganisationTag
import org.springframework.stereotype.Service

@Service
class CreateApiIntegration(
    private val repository: OrganisationRepository,
    private val contentPackageRepository: ContentPackageRepository
) {
    operator fun invoke(request: CreateOrganisationRequest): ApiIntegration {
        assertNewApiIntegrationDoesNotCollide(request)

        val name = request.name ?: throw IllegalStateException("Name cannot be null")
        val contentPackage = request.contentPackageId?.let { contentPackageRepository.findById(ContentPackageId(it)) }

        val tags = if (repository.findByTag(OrganisationTag.DEFAULT_ORGANISATION).isEmpty()) {
            setOf(OrganisationTag.DEFAULT_ORGANISATION)
        } else emptySet()

        val organisation = ApiIntegration(
            id = OrganisationId(),
            name = name,
            address = Address(),
            deal = Deal(
                contentAccess = contentPackage?.id?.let { ContentAccess.SimpleAccess(it) },
                billing = false,
                accessExpiresOn = null
            ),
            tags = tags,
            role = request.role,
            domain = null,
            allowsOverridingUserIds = false,
            features = null
        )

        return repository.save(organisation)
    }

    private fun assertNewApiIntegrationDoesNotCollide(request: CreateOrganisationRequest) {
        repository.findApiIntegrationByName(request.name!!)?.let {
            throw OrganisationAlreadyExistsException(request.name!!)
        }
        if (repository.findByRoleIn(listOf(request.role!!)).isNotEmpty()) {
            throw OrganisationAlreadyExistsException(request.role!!)
        }
    }
}
