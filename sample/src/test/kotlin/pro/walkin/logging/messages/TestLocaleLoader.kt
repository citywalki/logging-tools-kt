package pro.walkin.logging.messages

import pro.walkin.logging.spi.LocaleLoader
import java.util.*

class TestLocaleLoader : LocaleLoader {
    override fun getLocale(): Locale = Locale.SIMPLIFIED_CHINESE
}
