package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.BroadcastUsers
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/admin/users/actions")
class AdminController(
    private val broadcastUsers: BroadcastUsers
) {
    @PostMapping("/broadcast_users")
    fun broadcastUsersAction() {
        broadcastUsers()
    }
}
