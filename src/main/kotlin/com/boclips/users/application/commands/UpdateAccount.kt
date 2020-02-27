package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.AccountNotFoundException
import com.boclips.users.application.exceptions.InvalidDateException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.service.AccountExpiresOnUpdate
import com.boclips.users.domain.service.AccountRepository
import com.boclips.users.presentation.requests.UpdateAccountRequest
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Component
class UpdateAccount(private val accountRepository: AccountRepository) {
    operator fun invoke(id: String, request: UpdateAccountRequest?): Organisation<*> {
        if (!UserExtractor.currentUserHasRole(UserRoles.UPDATE_ORGANISATIONS)) {
            throw PermissionDeniedException()
        }

        val convertedDate = convertToZonedDateTime(request?.accessExpiresOn)

        return accountRepository.update(
            AccountExpiresOnUpdate(
                AccountId(id),
                convertedDate
            )
        ) ?: throw AccountNotFoundException(id)
    }

    private fun convertToZonedDateTime(date: String?): ZonedDateTime {
        try {
            return ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        } catch (e: DateTimeParseException) {
            throw InvalidDateException(date)
        }
    }
}
