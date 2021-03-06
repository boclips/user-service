package com.boclips.users.application.exceptions

import com.boclips.users.domain.model.user.UserId

class ShareCodeNotFoundException(val userId: UserId) : NotFoundException("Share code not found for user $userId ")
