package com.boclips.users.infrastructure.contract

import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "contracts")
sealed class ContractDocument {
    @TypeAlias("SelectedContent")
    data class SelectedContent(
        override val id: ObjectId,
        override val name: String,
        val collectionIds: List<String>
    ) : ContractDocument()

    abstract val id: ObjectId
    abstract val name: String
}
