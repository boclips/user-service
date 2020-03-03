package com.boclips.users.application.exceptions

import com.boclips.users.domain.model.UserId

class ContentPackageNotForUserFoundException(userId: UserId) :
    NotFoundException(message = "Could not find content package for user: ${userId.value}")
