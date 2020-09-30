package com.boclips.users.api.httpclient.test.fakes

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IntegrationsClientFakeTest {

    @Test
    fun `can synchronise user within organisation`() {
        val fake = IntegrationsClientFake()
        val user = fake.synchroniseUser(deploymentId = "deployment-id", externalUserId = "external-user-id")

        assertThat(user.userId).isNotNull
    }

    @Test
    fun `doesn't create duplicate users when synchronising multiple times`() {
        val fake = IntegrationsClientFake()
        val firstSynchedUser = fake.synchroniseUser(deploymentId = "deployment-id", externalUserId = "external-user-id")
        val secondSynchedUser = fake.synchroniseUser(deploymentId = "deployment-id", externalUserId = "external-user-id")

        assertThat(firstSynchedUser.userId).isEqualTo(secondSynchedUser.userId)
    }

    @Test
    fun `creates separate users when they have different external ids`() {
        val fake = IntegrationsClientFake()
        val firstSynchedUser = fake.synchroniseUser(deploymentId = "deployment-id", externalUserId = "external-user-id")
        val secondSynchedUser = fake.synchroniseUser(deploymentId = "deployment-id", externalUserId = "external-user-id2")

        assertThat(firstSynchedUser.userId).isNotEqualTo(secondSynchedUser.userId)
    }

    @Test
    fun `creates separate users when they have the same external ids but different deployment ids`() {
        val fake = IntegrationsClientFake()
        val firstSynchedUser = fake.synchroniseUser(deploymentId = "deployment-id", externalUserId = "external-user-id")
        val secondSynchedUser = fake.synchroniseUser(deploymentId = "deployment-id2", externalUserId = "external-user-id")

        assertThat(firstSynchedUser.userId).isNotEqualTo(secondSynchedUser.userId)
    }
}
