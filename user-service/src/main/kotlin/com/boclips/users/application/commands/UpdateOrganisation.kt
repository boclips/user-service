package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.users.api.request.UpdateOrganisationRequest
import com.boclips.users.application.exceptions.InvalidDateException
import com.boclips.users.application.exceptions.InvalidFeatureException
import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceBilling
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceContentPackageId
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceDomain
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceExpiryDate
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceFeatures
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Component
class UpdateOrganisation(private val organisationRepository: OrganisationRepository) {
    operator fun invoke(id: String, request: UpdateOrganisationRequest?): Organisation {
        if (!UserExtractor.currentUserHasRole(UserRoles.UPDATE_ORGANISATIONS)) {
            throw PermissionDeniedException()
        }

        val organisationId = OrganisationId(value = id)

        val expiryUpdate = request?.accessExpiresOn?.let { accessExpiryOn ->
            val convertedDate = convertToZonedDateTime(accessExpiryOn)
            ReplaceExpiryDate(convertedDate)
        }

        val domainUpdate = request?.domain?.let { domain ->
            ReplaceDomain(domain.trim())
        }

        val contentPackageIdUpdate = request?.contentPackageId?.let { contentPackageId ->
            ReplaceContentPackageId(ContentPackageId(contentPackageId))
        }

        val billingUpdate = request?.billing?.let { billing ->
            ReplaceBilling(billing)
        }

        val featuresUpdate = request?.features?.let { features ->
            val convertedFeatures = convertToFeatures(features)
            ReplaceFeatures(convertedFeatures)
        }

        organisationRepository.update(
            organisationId,
            *listOfNotNull(
                expiryUpdate,
                domainUpdate,
                featuresUpdate,
                contentPackageIdUpdate,
                billingUpdate
            ).toTypedArray()
        )
            ?: throw OrganisationNotFoundException(id)

        return organisationRepository.findOrganisationById(organisationId)
            ?: throw OrganisationNotFoundException(id)
    }

    private fun convertToZonedDateTime(date: String?): ZonedDateTime {
        try {
            return ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        } catch (e: DateTimeParseException) {
            throw InvalidDateException(date)
        }
    }

    private fun convertToFeatures(features: Map<String, Boolean>): Map<Feature, Boolean> {
        val invalidFeatures = features.filterKeys { !Feature.isValid(it) }
        if (invalidFeatures.isNotEmpty()) throw InvalidFeatureException(invalidFeatures.keys)

        return features.mapKeys { Feature.valueOf(it.key) }
    }
}
