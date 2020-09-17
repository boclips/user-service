package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.httpclient.ContentPackagesClient
import com.boclips.users.api.response.accessrule.ContentPackageResource

class ContentPackagesClientFake : ContentPackagesClient, FakeClient<ContentPackageResource> {
    private val database: MutableMap<String, ContentPackageResource> = LinkedHashMap()

    override fun find(id: String): ContentPackageResource =
        database[id] ?: throw FakeClient.forbiddenException(
            "Content package not found for ID $id"
        )

    override fun add(element: ContentPackageResource): ContentPackageResource {
        database[element.id] = element
        return element
    }

    override fun findAll(): List<ContentPackageResource> {
        return database.values.toList()
    }

    override fun clear() {
        database.clear()
    }
}
