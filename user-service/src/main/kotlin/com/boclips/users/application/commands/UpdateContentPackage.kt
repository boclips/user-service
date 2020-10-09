package com.boclips.users.application.commands

import com.boclips.users.api.request.UpdateContentPackageRequest
import com.boclips.users.application.exceptions.ContentPackageNotFoundException
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageRepository
import com.boclips.users.presentation.converters.ContentPackageConverter
import org.springframework.stereotype.Service

@Service
class UpdateContentPackage(
    private val contentPackageRepository: ContentPackageRepository,
    private val contentPackageConverter: ContentPackageConverter
) {
    operator fun invoke(id: String, updateContentPackageRequest: UpdateContentPackageRequest): ContentPackage {
        val updatedPackage = contentPackageConverter.toContentPackage(id, updateContentPackageRequest)
        return contentPackageRepository.replace(updatedPackage)
            ?: throw ContentPackageNotFoundException(id)
    }
}
