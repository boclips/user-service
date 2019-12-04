package com.boclips.users.application.exceptions

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

open class InvalidDateException(date: String?) : BoclipsApiException(
    ExceptionDetails(
        error = "Invalid request",
        message = "Could not parse date: ${date}.  Format should be ISO INSTANT.",
        status = HttpStatus.BAD_REQUEST
    )
)
