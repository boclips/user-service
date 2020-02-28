package com.boclips.users.domain.service

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.organisation.OrganisationId

interface UserRepository {
    fun create(identity: Identity): User
    fun create(user: User): User
    fun findById(id: UserId): User?
    fun findAll(ids: List<UserId>): List<User>
    fun findAll(): List<User>
    fun findAllByOrganisationId(id: OrganisationId): List<User>
    fun update(user: User, vararg updateCommands: UserUpdateCommand): User
}
