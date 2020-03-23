package com.boclips.users.application.exceptions

class AccessRuleExistsException(name: String) : AlreadyExistsException("AccessRule with name $name already exists")
