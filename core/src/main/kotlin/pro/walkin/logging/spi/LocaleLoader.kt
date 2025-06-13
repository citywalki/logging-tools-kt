package pro.walkin.logging.spi

import java.util.Locale

interface LocaleLoader {

    fun getLocale(): Locale
}
