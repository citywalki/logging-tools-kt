package pro.walkin.logging.processor

internal data class Config(
    val enable: Boolean,
    val translationFilesPath: String? = null
) {

    companion object{
        fun create(options: Map<String, String>): Config {

            val translationFilesPath = options["translationFilesPath"]
            return Config(
                enable = true,
                translationFilesPath=translationFilesPath
            )
        }
    }
}

