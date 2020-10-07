package com.boclips.users.application.exceptions

import com.boclips.users.domain.model.feature.Feature
import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class InvalidFeatureException(features: Collection<String>) : BoclipsApiException(
    ExceptionDetails(
        error = "Invalid feature",
        message = "$features not an existing feature, valid features are: ${Feature.values().map { it.name }}}",
        status = HttpStatus.BAD_REQUEST
    )
)