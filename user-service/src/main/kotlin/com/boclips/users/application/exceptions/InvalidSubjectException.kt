package com.boclips.users.application.exceptions

class InvalidSubjectException(subjects: List<String>) :
    RuntimeException("Subject: $subjects is invalid")
