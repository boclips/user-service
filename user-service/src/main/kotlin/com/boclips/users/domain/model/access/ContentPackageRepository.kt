package com.boclips.users.domain.model.access

interface ContentPackageRepository {
    fun save(contentPackage: ContentPackage): ContentPackage
    fun findById(id: ContentPackageId): ContentPackage?
    fun findByName(name: String): ContentPackage?
    fun findAll(): List<ContentPackage>
}
