package ru.boilercalc.app.core.util

import android.net.Uri

object UtmParser {
    private val utmParams = listOf("utm_source", "utm_medium", "utm_campaign", "utm_content", "utm_term")

    fun parse(uri: Uri?): Map<String, String> {
        if (uri == null) return emptyMap()
        return utmParams.mapNotNull { param ->
            uri.getQueryParameter(param)?.let { param to it }
        }.toMap()
    }

    fun parse(url: String?): Map<String, String> {
        if (url.isNullOrBlank()) return emptyMap()
        return try {
            parse(Uri.parse(url))
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
