package com.boclips.users.application.exceptions

class ContentPackageNotFoundException(contentPackageId: String) :
    NotFoundException(message = "Cannot find content package: $contentPackageId") {

}
