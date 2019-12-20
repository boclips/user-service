package com.boclips.users.presentation.projections

import com.boclips.security.utils.UserExtractor.currentUserHasRole
import com.boclips.users.config.security.UserRoles

interface ProjectionResolver {
    fun resolveProjection(): Class<out UserProjection>
}

class RoleBasedProjectionResolver : ProjectionResolver {
    override fun resolveProjection(): Class<out UserProjection> {
        return when {
            currentUserHasRole(UserRoles.ROLE_TEACHER) -> TeacherProjection::class.java
            currentUserHasRole(UserRoles.ROLE_API) -> ApiUserProjection::class.java
            else -> UserProjection::class.java
        }
    }
}

