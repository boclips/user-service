package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateDistrictRequest
import com.boclips.users.application.exceptions.OrganisationAlreadyExistsException
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.access.ContentPackageRepository
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ContentAccess
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import org.springframework.stereotype.Service

@Service
class CreateDistrict(
    private val repository: OrganisationRepository,
    private val contentPackageRepository: ContentPackageRepository
) {
    operator fun invoke(request: CreateDistrictRequest): District {
        assertNewDistrictDoesNotCollide(request)

        val contentPackage = request.contentPackageId?.let { contentPackageRepository.findById(ContentPackageId(it)) }

        val organisation = District(
            id = OrganisationId(),
            name = request.name,
            address = Address(),
            deal = Deal(
                contentAccess = contentPackage?.id?.let {ContentAccess.SimpleAccess(it)},
                billing = false,
                accessExpiresOn = null
            ),
            tags = emptySet(),
            domain = null,
            features = null,
            externalId = null,
            role = null
        )

        return repository.save(organisation)
    }

    private fun assertNewDistrictDoesNotCollide(request: CreateDistrictRequest) {
        repository.findDistrictByName(request.name)?.let {
            throw OrganisationAlreadyExistsException(request.name)
        }
    }
}
