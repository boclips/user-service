package com.boclips.users.keycloakclient

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

interface ContractTest {
    fun `getUserById`()

    fun `get invalid user`()

    fun `new user has not logged in before`()

    fun `can create and delete user`()
}