package com.boclips.users.application.exceptions

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class NotAuthenticatedException : BoclipsApiException(
    ExceptionDetails(
        error = HttpStatus.UNAUTHORIZED.reasonPhrase,
        message = "Not authenticated",
        status = HttpStatus.UNAUTHORIZED
    )
)
