package com.boclips.users.application.exceptions

import com.boclips.users.domain.model.user.UserId

class UserNotFoundException(val userId: UserId) : NotFoundException("User $userId not found")
