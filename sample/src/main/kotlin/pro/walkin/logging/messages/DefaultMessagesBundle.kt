package pro.walkin.logging.messages

import pro.walkin.logging.I18nMessages
import pro.walkin.logging.spi.LocaleLoader
import java.util.*

class DefaultMessagesBundle : DefaultMessages {

    companion object {
        public val defaultMessages = DefaultMessagesBundle()
    }

    fun getLoggingLocale(): Locale {
        val loader = ServiceLoader.load(LocaleLoader::class.java)
        val localeLoader = loader.firstOrNull()
        return localeLoader?.getLocale() ?: return Locale.getDefault()
    }

    override fun basic(): String {
        val locale = getLoggingLocale()
        var message = "default"

        if (localeEquals(locale,Locale.SIMPLIFIED_CHINESE)) {
            message = "你好"
        } else if (localeEquals(locale,Locale.ENGLISH)) {
            message = ""
        }

        return message
    }

    override fun basicException(): IllegalStateException {
        val locale = getLoggingLocale()
        var message = "default"

        if (locale.language == "zh_CN") {
            message = ""
        } else if (locale.language == "en") {
            message = ""
        }

        val exception = IllegalStateException(message)
        val st = exception.stackTrace
        exception.stackTrace = st.copyOfRange(1, st.size)
        return exception
    }

    override fun basic(world: String): String {
        val locale = getLoggingLocale()
        var message = "default".format(world)

        if (locale.language == "zh_CN") {
            message = "".format(world)
        } else if (locale.language == "en") {
            message = "".format(world)
        }

        return message
    }

    private fun localeEquals(left: Locale, right: Locale): Boolean = left.language == right.language && left.country == right.country
}

public val I18nMessages.defaultMessages: DefaultMessagesBundle
    get() = DefaultMessagesBundle()
