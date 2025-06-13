package pro.walkin.logging.messages

interface DefaultMessages {

    fun basic(): String

    fun basicException(): IllegalStateException

    fun basic(world: String): String
}
