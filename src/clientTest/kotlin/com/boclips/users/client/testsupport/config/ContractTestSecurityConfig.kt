package com.boclips.users.client.testsupport.config

import com.boclips.security.HttpSecurityConfigurer
import com.boclips.users.config.security.UserRoles
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@EnableWebSecurity
@Profile("contract-test")
@Configuration
class ContractTestSecurityConfig(
    val httpSecurityConfigurer: HttpSecurityConfigurer
) : WebSecurityConfigurerAdapter() {
    companion object {
        const val testUser = "user-service-client@boclips.com"
        const val testPassword = "reallySecurePassword123"
    }

    override fun configure(http: HttpSecurity) {
        http
            .httpBasic()
            .and().csrf().disable()
        httpSecurityConfigurer.configure(http)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
            .withUser(testUser)
            .password(passwordEncoder().encode(testPassword))
            .roles(
                UserRoles.VIEW_USERS,
                UserRoles.VIEW_ACCESS_RULES,
                UserRoles.ROLE_TEACHER,
                UserRoles.VIEW_ORGANISATIONS,
                UserRoles.VIEW_CONTENT_PACKAGES
            )
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

