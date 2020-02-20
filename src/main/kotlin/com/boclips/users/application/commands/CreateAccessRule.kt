package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccessRuleExistsException
import com.boclips.users.domain.model.accessrules.AccessRule
import com.boclips.users.domain.model.accessrules.CollectionId
import com.boclips.users.domain.model.accessrules.VideoId
import com.boclips.users.domain.service.AccessRuleRepository
import com.boclips.users.domain.service.SelectedContentAccessRuleRepository
import com.boclips.users.presentation.requests.CreateAccessRuleRequest
import org.springframework.stereotype.Service

@Service
class CreateAccessRule(
    private val selectedContentAccessRuleRepository: SelectedContentAccessRuleRepository,
    private val accessRuleRepository: AccessRuleRepository
) {
    operator fun invoke(request: CreateAccessRuleRequest): AccessRule {
        if (accessRuleAlreadyExists(request)) {
            throw AccessRuleExistsException(request.name!!)
        }

        return when (request) {
            is CreateAccessRuleRequest.SelectedCollections -> selectedContentAccessRuleRepository.saveSelectedCollectionsAccessRule(
                request.name!!,
                request.collectionIds!!.map { CollectionId(it) }
            )
            is CreateAccessRuleRequest.SelectedVideos -> selectedContentAccessRuleRepository.saveSelectedVideosAccessRule(
                request.name!!,
                request.videoIds!!.map { VideoId(it) }
            )
        }
    }

    private fun accessRuleAlreadyExists(request: CreateAccessRuleRequest): Boolean {
        return accessRuleRepository.findAllByName(request.name!!).isNotEmpty()
    }
}
