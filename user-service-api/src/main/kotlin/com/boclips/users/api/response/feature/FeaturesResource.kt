package com.boclips.users.api.response.feature

class FeaturesResource(val _embedded: FeaturesWrapper)

// TODO think about typing the keys here
class FeaturesWrapper(val features: FeatureResource)

typealias FeatureResource = Map<String, Boolean>
