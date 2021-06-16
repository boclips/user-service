package com.boclips.users.presentation.projections

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.api.BoclipsServiceProjection
import com.boclips.users.api.BoclipsWebAppProjection
import com.boclips.users.api.LtiProjection
import com.boclips.users.api.TeacherProjection
import com.boclips.users.api.UserProjection
import com.boclips.users.config.security.UserRoles.ROLE_BOCLIPS_SERVICE
import com.boclips.users.config.security.UserRoles.ROLE_BOCLIPS_WEB_APP
import com.boclips.users.config.security.UserRoles.ROLE_BOCLIPS_WEB_APP_DEMO
import com.boclips.users.config.security.UserRoles.ROLE_LTI
import com.boclips.users.config.security.UserRoles.ROLE_TEACHER
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream
import kotlin.reflect.KClass

class ProjectionResolverTest {

    companion object {
        val testCases = Stream.of(
            arrayOf(ROLE_BOCLIPS_WEB_APP, ROLE_BOCLIPS_WEB_APP_DEMO) to BoclipsWebAppProjection::class,
            arrayOf(ROLE_TEACHER) to TeacherProjection::class,
            arrayOf(ROLE_BOCLIPS_SERVICE) to BoclipsServiceProjection::class,
            arrayOf(ROLE_LTI) to LtiProjection::class,
            emptyArray<String>() to UserProjection::class
        )
    }

    @ParameterizedTest
    @ArgumentsSource(ProjectionResolverProvider::class)
    fun `maps role(s) to projection`(roles: Array<String>, projection: KClass<*>) {
        setSecurityContext("user", *roles)

        assertThat(RoleBasedProjectionResolver().resolveProjection()).isEqualTo(projection.java)
    }

    class ProjectionResolverProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<Arguments>? {
            return ProjectionResolverTest.testCases.map { Arguments.of(it.first, it.second) }
        }
    }
}
