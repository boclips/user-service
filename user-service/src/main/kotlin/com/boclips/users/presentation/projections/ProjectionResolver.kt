package com.boclips.users.presentation.projections

import com.boclips.security.utils.UserExtractor.currentUserHasAnyRole
import com.boclips.security.utils.UserExtractor.currentUserHasRole
import com.boclips.users.api.BoclipsServiceProjection
import com.boclips.users.api.LtiProjection
import com.boclips.users.api.BoclipsWebAppProjection
import com.boclips.users.api.TeacherProjection
import com.boclips.users.api.UserProjection
import com.boclips.users.config.security.UserRoles

interface ProjectionResolver {
    fun resolveProjection(): Class<out UserProjection>
}

class RoleBasedProjectionResolver : ProjectionResolver {
    override fun resolveProjection(): Class<out UserProjection> {
        return when {
            currentUserHasRole(UserRoles.ROLE_TEACHER) -> TeacherProjection::class.java
            currentUserHasAnyRole(UserRoles.ROLE_BOCLIPS_WEB_APP, UserRoles.ROLE_BOCLIPS_WEB_APP_DEMO) -> BoclipsWebAppProjection::class.java
            currentUserHasRole(UserRoles.ROLE_BOCLIPS_SERVICE) -> BoclipsServiceProjection::class.java
            currentUserHasRole(UserRoles.ROLE_LTI) -> LtiProjection::class.java
            else -> UserProjection::class.java
        }
    }
}
