package com.boclips.users.infrastructure.access
import org.bson.types.ObjectId

data class AccessRuleDocument(
    var _id: ObjectId,
    var name: String,
    var _class: String,
    var collectionIds: List<String>? = null,
    var videoIds: List<String>? = null,
    var videoTypes: List<VideoTypeDocument>? = null,
    var contentPartnerIds: List<String>? = null,
    var distributionMethods: List<String>? = null
) {

    companion object {
        const val TYPE_INCLUDED_COLLECTIONS = "IncludedCollections"
        const val TYPE_INCLUDED_VIDEOS = "IncludedVideos"
        const val TYPE_EXCLUDED_VIDEOS = "ExcludedVideos"
        const val TYPE_EXCLUDED_VIDEO_TYPES = "ExcludedVideoTypes"
        const val TYPE_EXCLUDED_CONTENT_PARTNERS = "ExcludedContentPartners"
        const val TYPE_INCLUDED_DISTRIBUTION_METHODS = "IncludedDistributionMethods"

        const val DISTRIBUTION_METHOD_STREAM = "STREAM"
        const val DISTRIBUTION_METHOD_DOWNLOAD = "DOWNLOAD"
    }
}
