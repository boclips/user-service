package com.boclips.users.application

import com.boclips.security.utils.User
import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.UserRepository
import com.boclips.users.presentation.SecurityContextUserNotFoundException
import com.boclips.users.presentation.UserResource
import org.springframework.hateoas.EntityLinks
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resource
import org.springframework.stereotype.Component

@Component
class UserActions(
        private val userRepository: UserRepository,
        private val entityLinks: EntityLinks
) {

    fun getLinks() = Resource("", UserExtractor.getCurrentUser()
            ?.let { buildLinksForUser(it) }
            ?: emptyList<Link>())

    fun activateUser() = UserExtractor.getCurrentUser()
            ?.let { userRepository.save(com.boclips.users.domain.model.User(id = it.id, activated = true)) }
            ?.let { Resource("", entityLinks.linkToSingleResource(UserResource(it.id)).withSelfRel()) }
            ?: throw SecurityContextUserNotFoundException()

    private fun buildLinksForUser(currentUser: User) = userRepository
            .findById(currentUser.id)
            ?.takeIf { it.activated }
            ?.let { UserResource.from(it) }
            ?.let { listOf(entityLinks.linkToSingleResource(it).withRel("profile")) }
            ?: listOf(entityLinks.linkToCollectionResource(UserResource::class.java).withRel("activate"))
}