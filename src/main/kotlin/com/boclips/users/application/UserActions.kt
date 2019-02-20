package com.boclips.users.application

import com.boclips.security.utils.User
import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.presentation.SecurityContextUserNotFoundException
import com.boclips.users.presentation.UserResource
import org.springframework.hateoas.EntityLinks
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resource
import org.springframework.stereotype.Component

@Component
class UserActions(
    private val userService: UserService,
    private val accountRepository: AccountRepository,
    private val entityLinks: EntityLinks
) {

    fun getLinks() = Resource("", UserExtractor.getCurrentUser()
        ?.let { buildLinksForUser(it) }
        ?: emptyList<Link>())

    fun activateUser() = UserExtractor.getCurrentUser()
        ?.let { userService.activate(it.id) }
        ?.let { Resource("", entityLinks.linkToSingleResource(UserResource(it.id.value)).withSelfRel()) }
        ?: throw SecurityContextUserNotFoundException()

    private fun buildLinksForUser(currentUser: User) = accountRepository.findById(AccountId(value = currentUser.id))
        ?.takeIf { it.activated }
        ?.let { UserResource.from(it) }
        ?.let { listOf(entityLinks.linkToSingleResource(it).withRel("profile")) }
        ?: listOf(entityLinks.linkToCollectionResource(UserResource::class.java).withRel("activate"))
}