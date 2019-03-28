package com.boclips.users.domain.model

class UserNotFoundException(userId: UserId) : RuntimeException("User $userId not found")
