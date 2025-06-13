package pro.walkin.logging.messages

import pro.walkin.logging.annotations.Message
import pro.walkin.logging.annotations.MessageBundle

//@MessageBundle("DEFAULT")
interface DefaultMessages {

    @Message(111)
    fun basic(): String

    @Message(222)
    fun basicException(): IllegalStateException

    fun basic(world: String): String
}
