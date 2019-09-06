package com.boclips.users.application.exceptions

import java.lang.RuntimeException

class ContractExistsException(name: String) : RuntimeException("Contract with name $name already exists")