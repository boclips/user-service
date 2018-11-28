package com.boclips.users.domain.model

interface UserRepository {
    fun save(user: User): User
    fun findById(id: String): User?
}