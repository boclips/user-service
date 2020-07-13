package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateAccessRuleRequest
import com.boclips.users.application.exceptions.AccessRuleExistsException
import com.boclips.users.application.exceptions.InvalidVideoTypeException
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.AccessRuleRepository
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.service.UniqueId
import org.springframework.stereotype.Service

@Service
class CreateAccessRule(
    private val accessRuleRepository: AccessRuleRepository
) {
    operator fun invoke(request: CreateAccessRuleRequest): AccessRule {
        val name = request.name!!
        val id = AccessRuleId(UniqueId())

        if (accessRuleAlreadyExists(name)) {
            throw AccessRuleExistsException(name)
        }

        return when (request) {
            is CreateAccessRuleRequest.IncludedCollections -> accessRuleRepository.save(AccessRule.IncludedCollections(
                id = id,
                name = name,
                collectionIds = request.collectionIds!!.map { CollectionId(it) }
            ))
            is CreateAccessRuleRequest.IncludedVideos -> accessRuleRepository.save(AccessRule.IncludedVideos(
                id = id,
                name = name,
                videoIds = request.videoIds!!.map { VideoId(it) }
            ))
            is CreateAccessRuleRequest.ExcludedVideoTypes -> accessRuleRepository.save(
                AccessRule.ExcludedVideoTypes(
                    id = id,
                    name = name,
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
            is CreateAccessRuleRequest.IncludedChannels -> accessRuleRepository.save(AccessRule.IncludedChannels(
                id = id,
                name = name,
                channelIds = request.channelIds!!.map { ChannelId(it) }
            ))
        }
    }

    private fun accessRuleAlreadyExists(name: String): Boolean {
        return accessRuleRepository.findAllByName(name).isNotEmpty()
    }
}
