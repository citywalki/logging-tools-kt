package pro.walkin.logging.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Message(
    val value: String,
    val code: Int = INHERIT,
    val format: Format = Format.PRINTF
) {
    companion object {
        /**
         * Indicates that this message has no ID.
         */
        const val NONE: Int = 0

        /**
         * Indicates that this message should inherit the ID from another message with the same name.
         */
        const val INHERIT: Int = -1
    }

    enum class Format {
        /**
         * A [java.util.Formatter]-type format string.
         */
        PRINTF,

        /**
         * A [java.text.MessageFormat]-type format string.
         */
        MESSAGE_FORMAT,

        /**
         * Indicates the message should not be formatted.
         */
        NO_FORMAT
    }
}
