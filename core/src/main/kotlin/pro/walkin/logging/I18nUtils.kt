package pro.walkin.logging

import java.util.*

object I18nUtils {
    fun localeEquals(current: Locale, targetStr: String): Boolean {
        val target = when (targetStr) {
            "en_CA" -> {
                Locale.CANADA
            }

            "fr_CA" -> {
                Locale.CANADA_FRENCH
            }

            "zh" -> {
                Locale.CHINESE
            }

            "en" -> {
                Locale.ENGLISH
            }

            "fr_FR" -> {
                Locale.FRENCH
            }

            else -> {
                val parts = targetStr.split('_')
                if (parts.size > 3) {
                    throw IllegalArgumentException("Failed to parse %s to a Locale. $targetStr")
                }
                Locale(parts[0], parts[1])
            }
        }

        return localeEquals(current, target)
    }

    fun localeEquals(current: Locale, target: Locale): Boolean {
        return target == current
    }
}
