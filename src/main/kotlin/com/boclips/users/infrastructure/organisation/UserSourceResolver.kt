package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.OrganisationType

interface UserSourceResolver {
    fun resolve(roles: List<String>): OrganisationType?
}
