package com.boclips.users.presentation.projections

import com.boclips.security.utils.UserExtractor.currentUserHasRole
import com.boclips.users.api.ApiUserProjection
import com.boclips.users.api.BoclipsServiceProjection
import com.boclips.users.api.LtiProjection
import com.boclips.users.api.PublisherProjection
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
            currentUserHasRole(UserRoles.ROLE_PUBLISHER) -> PublisherProjection::class.java
            currentUserHasRole(UserRoles.ROLE_API) -> ApiUserProjection::class.java
            currentUserHasRole(UserRoles.ROLE_BOCLIPS_SERVICE) -> BoclipsServiceProjection::class.java
            currentUserHasRole(UserRoles.ROLE_LTI) -> LtiProjection::class.java
            else -> UserProjection::class.java
        }
    }
}
