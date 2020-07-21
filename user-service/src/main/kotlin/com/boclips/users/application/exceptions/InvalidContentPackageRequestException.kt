package com.boclips.users.application.exceptions

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class InvalidContentPackageRequestException(message: String) : BoclipsApiException(
    ExceptionDetails(
        error = HttpStatus.BAD_REQUEST.reasonPhrase,
        message = message,
        status = HttpStatus.BAD_REQUEST
    )
)
