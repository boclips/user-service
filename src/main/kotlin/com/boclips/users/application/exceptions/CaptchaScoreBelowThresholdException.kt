package com.boclips.users.application.exceptions

class CaptchaScoreBelowThresholdException(val identifier: String) :
    RuntimeException("User $identifier has a captcha score below the configured threshold")