package pro.walkin.logging.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class MessageBundle(

    val projectCode: String,
    val length: Int = 6,
    val aliases: Array<String> = [],

)
