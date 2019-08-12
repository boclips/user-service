package com.boclips.users.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "user-service")
class UserServiceProperties {
    lateinit var organisationMappings: List<OrganisationMapping>
}

class OrganisationMapping {
    lateinit var role: String
    lateinit var organisationName: String
}