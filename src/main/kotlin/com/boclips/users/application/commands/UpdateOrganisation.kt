package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.InvalidDateException
import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.service.OrganisationAccountExpiresOnUpdate
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.presentation.requests.UpdateOrganisationRequest
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Component
class UpdateOrganisation(private val organisationAccountRepository: OrganisationAccountRepository) {
    operator fun invoke(id: String, request: UpdateOrganisationRequest): OrganisationAccount<*> {
        val convertedDate = convertToZonedDateTime(request.accessExpiresOn)

        return organisationAccountRepository.update(
            OrganisationAccountExpiresOnUpdate(
                OrganisationAccountId(id),
                convertedDate
            )
        ) ?: throw OrganisationNotFoundException(id)
    }

    private fun convertToZonedDateTime(date: String?): ZonedDateTime {
        try {
            return ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        } catch (e: DateTimeParseException) {
            throw InvalidDateException(date)
        }
    }
}
