package com.boclips.users.infrastructure.keycloak.client

import mu.KLogging
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.Executors
import java.util.function.Consumer

class KeycloakDbProxy {
    companion object : KLogging()

    private var proxyProcess: Process? = null

    fun setupProxy(connectionName: String, proxyPort: String) {
        logger.info { "KeycloakDbConnector starting" }

        try {
            proxyProcess = Runtime.getRuntime().exec(
                "./cloud_sql_proxy -instances=$connectionName=tcp:$proxyPort"
            )
            Executors.newSingleThreadExecutor().submit(stream(proxyProcess!!.inputStream, System.out::println))
            Executors.newSingleThreadExecutor().submit(stream(proxyProcess!!.errorStream, System.out::println))
            val untilProxyKicksIn: Long = 10_000
            Thread.sleep(untilProxyKicksIn)
        } catch (e: Exception) {
            logger.error { "Setting up proxy to Keycloak db failed. Reason: $e" }
        }
    }

    fun closeProxy() {
        proxyProcess?.destroy()
    }

    private fun stream(inputStream: InputStream, consumer: Consumer<String>) = Runnable {
        BufferedReader(InputStreamReader(inputStream))
            .lines()
            .forEach(consumer)
    }
}
