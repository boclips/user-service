package com.boclips.users.keycloakclient

interface ContractTest {
    fun `getUserById`()

    fun `get invalid user`()

    fun `new user has not logged in before`()

    fun `can create and delete user`()
}