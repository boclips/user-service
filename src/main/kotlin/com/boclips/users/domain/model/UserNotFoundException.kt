package com.boclips.users.domain.model

class UserNotFoundException(val userId: UserId) : RuntimeException("User $userId not found")
