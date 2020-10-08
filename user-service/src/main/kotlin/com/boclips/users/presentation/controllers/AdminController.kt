package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.BroadcastContentPackages
import com.boclips.users.application.commands.BroadcastUsers
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/admin/users/actions")
class AdminController(
    private val broadcastUsers: BroadcastUsers,
    private val broadcastContentPackages: BroadcastContentPackages
) {
    @PostMapping("/broadcast_users")
    fun broadcastUsersAction() =
        broadcastUsers()

    @PostMapping("/broadcast_content_packages")
    fun broadcastContentPackagesAction() =
        broadcastContentPackages()
}
