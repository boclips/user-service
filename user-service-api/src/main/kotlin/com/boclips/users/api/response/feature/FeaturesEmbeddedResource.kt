package com.boclips.users.api.response.feature

class FeaturesEmbeddedResource(val _embedded: FeaturesWrapper)

class FeaturesWrapper(val features: FeaturesResource)

typealias FeaturesResource = Map<String, Boolean>
