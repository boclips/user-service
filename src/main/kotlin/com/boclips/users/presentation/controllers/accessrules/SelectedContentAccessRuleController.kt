package com.boclips.users.presentation.controllers.accessrules

import com.boclips.users.application.commands.AddCollectionToAccessRule
import com.boclips.users.application.commands.RemoveCollectionFromAccessRule
import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.domain.model.accessrules.CollectionId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/selected-content-access-rules")
class SelectedContentAccessRuleController(
    private val addCollectionToAccessRule: AddCollectionToAccessRule,
    private val removeCollectionFromAccessRule: RemoveCollectionFromAccessRule
) {
    @PutMapping("/{accessRuleId}/collections/{collectionId}")
    fun addCollection(@PathVariable accessRuleId: String, @PathVariable collectionId: String?): ResponseEntity<Any> {
        addCollectionToAccessRule(AccessRuleId(accessRuleId), CollectionId(collectionId!!))

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @DeleteMapping("/{accessRuleId}/collections/{collectionId}")
    fun removeCollection(@PathVariable accessRuleId: String, @PathVariable collectionId: String?): ResponseEntity<Any> {
        removeCollectionFromAccessRule(AccessRuleId(accessRuleId), CollectionId(collectionId!!))

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}