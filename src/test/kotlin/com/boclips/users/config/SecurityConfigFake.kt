package com.boclips.users.config

import com.boclips.security.testing.MockBoclipsSecurity
import org.springframework.context.annotation.Profile

@Profile("test & !contract-test")
@MockBoclipsSecurity
class SecurityConfigFake