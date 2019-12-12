package com.boclips.users.application.exceptions

import com.boclips.users.domain.model.UserId

class IdentityNotFoundException(id: UserId) : RuntimeException("Identity $id cannot be found")
