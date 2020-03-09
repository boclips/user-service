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

            .antMatchers(HttpMethod.POST, "/v1/events/*").permitAll()

            .antMatchers(HttpMethod.POST, "/v1/users").permitAll()
            .antMatchers(HttpMethod.PUT, "/v1/users/*").authenticated()
            .antMatchers(HttpMethod.POST, "/v1/users/sync").hasRole(UserRoles.SYNCHRONIZE_USERS_HUBSPOT)
            .antMatchers(HttpMethod.POST, "/v1/users/sync-identities").hasRole(UserRoles.SYNCHRONIZE_USERS_KEYCLOAK)
            .antMatchers(HttpMethod.GET, "/v1/users/*/content-package").hasRole(UserRoles.VIEW_CONTENT_PACKAGES)
            .antMatchers(HttpMethod.GET, "/v1/users/*/shareCode/*").permitAll()

            .antMatchers(HttpMethod.POST, "/v1/access-rules").hasRole(UserRoles.INSERT_ACCESS_RULES)
            .antMatchers(HttpMethod.GET, "/v1/access-rules").hasRole(UserRoles.VIEW_ACCESS_RULES)
            .antMatchers(HttpMethod.GET, "/v1/access-rules/*").hasRole(UserRoles.VIEW_ACCESS_RULES)

            .antMatchers(HttpMethod.PUT, "/v1/selected-content-access-rules/**").hasRole(UserRoles.UPDATE_ACCESS_RULES)
            .antMatchers(HttpMethod.DELETE, "/v1/selected-content-access-rules/**").hasRole(UserRoles.UPDATE_ACCESS_RULES)

            .antMatchers(HttpMethod.POST, "/v1/api-integrations").hasRole(UserRoles.INSERT_ORGANISATIONS)
            .antMatchers(HttpMethod.GET, "/v1/api-integrations").hasRole(UserRoles.VIEW_ORGANISATIONS)

            .antMatchers(HttpMethod.GET, "/v1/accounts").hasRole(UserRoles.VIEW_ORGANISATIONS)
            .antMatchers(HttpMethod.GET, "/v1/accounts/*").hasRole(UserRoles.VIEW_ORGANISATIONS)
            .antMatchers(HttpMethod.PUT, "/v1/accounts/*").hasRole(UserRoles.UPDATE_ORGANISATIONS)

            .antMatchers(HttpMethod.GET, "/v1/organisations").hasRole(UserRoles.VIEW_ORGANISATIONS)
            .antMatchers(HttpMethod.GET, "/v1/organisations/*").hasRole(UserRoles.VIEW_ORGANISATIONS)
            .antMatchers(HttpMethod.PUT, "/v1/organisations/*").hasRole(UserRoles.UPDATE_ORGANISATIONS)

            .antMatchers(HttpMethod.POST, "/v1/content-packages").hasRole(UserRoles.INSERT_CONTENT_PACKAGES)
            .antMatchers(HttpMethod.GET, "/v1/content-packages/*").hasRole(UserRoles.VIEW_CONTENT_PACKAGES)

            .anyRequest().authenticated()
    }
}
