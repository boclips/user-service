package com.boclips.users.config.security

import com.boclips.security.HttpSecurityConfigurer
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.stereotype.Component

@Component
class RestSecurityConfig : HttpSecurityConfigurer {
    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/actuator/health").permitAll()

            .antMatchers(HttpMethod.OPTIONS, "/v1/**").permitAll()

            .antMatchers("/v1").permitAll()
            .antMatchers("/v1/").permitAll()

            .antMatchers(HttpMethod.POST, "/v1/users").permitAll()
            .antMatchers(HttpMethod.POST, "/v1/users/activate").authenticated()
            .antMatchers(HttpMethod.POST, "/v1/users/sync").hasRole(UserRoles.SYNCHRONIZE_USERS_HUBSPOT)

            .anyRequest().denyAll()
    }
}

