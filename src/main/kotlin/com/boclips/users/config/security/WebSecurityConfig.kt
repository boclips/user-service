package com.boclips.users.config.security

import com.boclips.security.EnableBoclipsSecurity
import com.boclips.security.HttpSecurityConfigurer
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.stereotype.Component

@Profile("!test")
@Configuration
@EnableBoclipsSecurity
class WebSecurityConfig

@Component
class ApiSecurityConfig : HttpSecurityConfigurer {
    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/actuator/health").permitAll()
            .antMatchers(HttpMethod.GET, "/actuator/prometheus").permitAll()

            .antMatchers(HttpMethod.OPTIONS, "/v1/**").permitAll()

            .antMatchers("/v1").permitAll()
            .antMatchers("/v1/").permitAll()

            .antMatchers(HttpMethod.POST, "/v1/users").permitAll()
            .antMatchers(HttpMethod.PUT, "/v1/users/*").authenticated()
            .antMatchers(HttpMethod.POST, "/v1/users/sync").hasRole(UserRoles.SYNCHRONIZE_USERS_HUBSPOT)
            .antMatchers(HttpMethod.POST, "/v1/users/sync-identities").hasRole(UserRoles.SYNCHRONIZE_USERS_KEYCLOAK)
            .antMatchers(HttpMethod.POST, "/v1/users/replay-user-created-events").permitAll()

            .anyRequest().authenticated()
    }
}
