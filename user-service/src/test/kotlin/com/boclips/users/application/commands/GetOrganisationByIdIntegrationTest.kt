package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class GetOrganisationByIdIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var getOrganisationById: GetOrganisationById

    @Test
    fun `throws when organisation cannot be found`() {
        assertThrows<OrganisationNotFoundException> {
            getOrganisationById.invoke(ObjectId.get().toHexString())
        }
    }
}
