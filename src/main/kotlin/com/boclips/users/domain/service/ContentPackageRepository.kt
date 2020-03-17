package com.boclips.users.domain.service

import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.contentpackage.ContentPackageId

interface ContentPackageRepository {
    fun save(contentPackage: ContentPackage): ContentPackage
    fun findById(id: ContentPackageId): ContentPackage?
    fun findByName(name: String): ContentPackage?
}
