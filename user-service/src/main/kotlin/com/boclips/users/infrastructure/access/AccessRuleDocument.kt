package com.boclips.users.infrastructure.access

data class AccessRuleDocument(
    var id: String,
    var name: String,
    var _class: String,
    var collectionIds: List<String>? = null,
    var videoIds: List<String>? = null,
    var videoTypes: List<VideoTypeDocument>? = null,
    var distributionMethods: List<String>? = null,
    var channelIds: List<String>? = null,
    var voiceTypes: List<VideoVoiceTypeDocument>? = null,
    var languages: Set<String>? = null,
    var sources: Set<String>? = null,
) {

    companion object {
        const val TYPE_EXCLUDED_PLAYBACK_SOURCES = "ExcludedPlaybackSources"
        const val TYPE_INCLUDED_COLLECTIONS = "IncludedCollections"
        const val TYPE_INCLUDED_VIDEOS = "IncludedVideos"
        const val TYPE_EXCLUDED_VIDEOS = "ExcludedVideos"
        const val TYPE_EXCLUDED_VIDEO_TYPES = "ExcludedVideoTypes"
        const val TYPE_INCLUDED_VIDEO_TYPES = "IncludedVideoTypes"
        const val TYPE_EXCLUDED_CHANNELS = "ExcludedChannels"
        const val TYPE_INCLUDED_DISTRIBUTION_METHODS = "IncludedDistributionMethods"
        const val TYPE_INCLUDED_CHANNELS = "IncludedChannels"
        const val TYPE_INCLUDED_VIDEO_VOICE_TYPES = "IncludedVideoVoiceTypes"
        const val TYPE_EXCLUDED_LANGUAGES = "ExcludedLanguages"

        const val DISTRIBUTION_METHOD_STREAM = "STREAM"
        const val DISTRIBUTION_METHOD_DOWNLOAD = "DOWNLOAD"
    }
}
