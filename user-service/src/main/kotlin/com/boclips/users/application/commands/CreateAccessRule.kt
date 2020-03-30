package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateAccessRuleRequest
import com.boclips.users.application.exceptions.AccessRuleExistsException
import com.boclips.users.application.exceptions.InvalidVideoTypeException
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.model.contentpackage.VideoType
import com.boclips.users.domain.service.AccessRuleRepository
import com.boclips.users.domain.service.IncludedContentAccessRuleRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class CreateAccessRule(
    private val includedContentAccessRuleRepository: IncludedContentAccessRuleRepository,
    private val accessRuleRepository: AccessRuleRepository
) {
    operator fun invoke(request: CreateAccessRuleRequest): AccessRule {
        if (accessRuleAlreadyExists(request)) {
            throw AccessRuleExistsException(request.name!!)
        }

        return when (request) {
            is CreateAccessRuleRequest.IncludedCollections -> includedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                request.name!!,
                request.collectionIds!!.map { CollectionId(it) }
            )
            is CreateAccessRuleRequest.IncludedVideos -> includedContentAccessRuleRepository.saveIncludedVideosAccessRule(
                request.name!!,
                request.videoIds!!.map { VideoId(it) }
            )
            is CreateAccessRuleRequest.ExcludedVideoTypes -> accessRuleRepository.save(
                AccessRule.ExcludedVideoTypes(
                    id = AccessRuleId(ObjectId().toHexString()),
                    name = request.name!!,
                    videoTypes = request.videoTypes!!.map {
                        when (it.toUpperCase()) {
                            "NEWS" -> VideoType.NEWS
                            "INSTRUCTIONAL" -> VideoType.INSTRUCTIONAL
                            "STOCK" -> VideoType.STOCK
                            else -> throw InvalidVideoTypeException(it)
                        }
                    }
                )
            )
        }
    }

    private fun accessRuleAlreadyExists(request: CreateAccessRuleRequest): Boolean {
        return accessRuleRepository.findAllByName(request.name!!).isNotEmpty()
    }
}
