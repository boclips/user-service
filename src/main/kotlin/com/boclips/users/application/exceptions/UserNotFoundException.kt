package com.boclips.users.application.exceptions

import com.boclips.users.domain.model.UserId

class UserNotFoundException(val userId: UserId) : RuntimeException("User $userId not found")
