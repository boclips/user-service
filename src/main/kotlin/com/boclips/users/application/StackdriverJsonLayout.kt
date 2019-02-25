package com.boclips.users.application

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.contrib.jackson.JacksonJsonFormatter
import ch.qos.logback.contrib.json.classic.JsonLayout
import java.util.Arrays
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.concurrent.TimeUnit

/**
 * This class provides a JSON layout for a Logback appender compatible to the Stackdriver
 * log format.
 *
 * Reference: https://cloud.google.com/logging/docs/agent/configuration#process-payload
 */
class StackdriverJsonLayout : JsonLayout() {
    private var isIncludeSpanId = false
    private var isIncludeExceptionInMessage = false

    init {
        this.appendLineSeparator = true
        this.isIncludeExceptionInMessage = true
        this.includeException = false
        this.isIncludeSpanId = true
        setJsonFormatter(JacksonJsonFormatter())
    }

    override fun toJsonMap(event: ILoggingEvent): Map<String, Any> {
        val map = LinkedHashMap<String, Any>()

        if (this.includeMDC) {
            event.mdcPropertyMap.forEach { key, value ->
                if (!FILTERED_MDC_FIELDS.contains(key)) {
                    map[key] = value
                }
            }
        }
        if (this.includeTimestamp) {
            map[TIMESTAMP_SECONDS_ATTRIBUTE] =
                TimeUnit.MILLISECONDS.toSeconds(event.timeStamp)
            map[TIMESTAMP_NANOS_ATTRIBUTE] =
                TimeUnit.MILLISECONDS.toNanos(event.timeStamp % 1000)
        }

        add(
            SEVERITY_ATTRIBUTE, this.includeLevel,
            event.level.toString(), map
        )
        add(JsonLayout.THREAD_ATTR_NAME, this.includeThreadName, event.threadName, map)
        add(JsonLayout.LOGGER_ATTR_NAME, this.includeLoggerName, event.loggerName, map)

        if (this.includeFormattedMessage) {
            var message = event.formattedMessage
            if (this.isIncludeExceptionInMessage) {
                val throwableProxy = event.throwableProxy
                if (throwableProxy != null) {
                    val stackTrace = this.throwableProxyConverter.convert(event)
                    if (stackTrace != null && stackTrace != "") {
                        message += "\n" + stackTrace
                    }
                }
            }
            map[JsonLayout.FORMATTED_MESSAGE_ATTR_NAME] = message
        }
        add(JsonLayout.MESSAGE_ATTR_NAME, this.includeMessage, event.message, map)
        add(JsonLayout.CONTEXT_ATTR_NAME, this.includeContextName, event.loggerContextVO.name, map)
        addThrowableInfo(JsonLayout.EXCEPTION_ATTR_NAME, this.includeException, event, map)
        add(
            SPAN_ID_ATTRIBUTE, this.isIncludeSpanId,
            event.mdcPropertyMap[MDC_FIELD_SPAN_ID], map
        )
        addCustomDataToJsonMap(map, event)
        return map
    }

    companion object {
        const val SEVERITY_ATTRIBUTE = "severity"
        const val TIMESTAMP_SECONDS_ATTRIBUTE = "timestampSeconds"
        const val TIMESTAMP_NANOS_ATTRIBUTE = "timestampNanos"
        const val SPAN_ID_ATTRIBUTE = "logging.googleapis.com/spanId"
        const val MDC_FIELD_SPAN_ID = "X-B3-SpanId"
        private const val MDC_FIELD_TRACE_ID = "X-B3-TraceId"
        private const val MDC_FIELD_SPAN_EXPORT = "X-Span-Export"

        private val FILTERED_MDC_FIELDS = HashSet(
            Arrays.asList(
                MDC_FIELD_TRACE_ID,
                MDC_FIELD_SPAN_ID,
                MDC_FIELD_SPAN_EXPORT
            )
        )
    }
}
