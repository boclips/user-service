package com.boclips.users.domain.service

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.organisation.OrganisationAccountId

interface UserRepository {
    fun create(account: Account): User
    fun create(user: User): User
    fun findById(id: UserId): User?
    fun findAll(ids: List<UserId>): List<User>
    fun findAll(): List<User>
    fun findAllByOrganisationId(organisationId: OrganisationAccountId): List<User>
    fun update(user: User, vararg updateCommands: UserUpdateCommand): User
}
