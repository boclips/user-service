package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ContentPackageNotForUserFoundException
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.ContentPackageRepository
import com.boclips.users.domain.service.OrganisationRepository
import org.springframework.stereotype.Service

@Service
class GetContentPackageOfUser(
    private val organisationRepository: OrganisationRepository,
    private val getOrImportUser: GetOrImportUser,
    private val contentPackageRepository: ContentPackageRepository
) {
    operator fun invoke(userId: String): ContentPackage {
        return UserId(value = userId).let {
            findOrganisationOfUser(it)?.contentPackageId?.let { contentPackageId ->
                contentPackageRepository.findById(contentPackageId)
            } ?: throw ContentPackageNotForUserFoundException(it)
        }
    }

    private fun findOrganisationOfUser(userId: UserId): Organisation<*>? {
        return getOrImportUser(userId).organisationId?.let {
            organisationRepository.findOrganisationById(it)
        }
    }
}
