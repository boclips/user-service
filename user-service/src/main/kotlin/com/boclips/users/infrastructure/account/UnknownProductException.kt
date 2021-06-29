package com.boclips.users.infrastructure.account

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

open class UnknownProductException(message: String) : BoclipsApiException(
    ExceptionDetails(
        error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
        message = message,
        status = HttpStatus.INTERNAL_SERVER_ERROR
    )
)
