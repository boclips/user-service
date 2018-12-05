package com.boclips.users.domain.model.users

interface UserRepository {
    fun save(user: User): User
    fun findById(id: String): User?
}