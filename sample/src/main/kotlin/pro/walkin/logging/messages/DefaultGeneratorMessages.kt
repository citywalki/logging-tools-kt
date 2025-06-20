package pro.walkin.logging.messages

import pro.walkin.logging.annotations.Message
import pro.walkin.logging.annotations.MessageBundle

@MessageBundle("DEFAULT")
interface DefaultGeneratorMessages {

    @Message(code = 111, value = "hello world")
    fun basic(): String

    @Message(code = 222, value = "hello exception")
    fun basicException(): IllegalStateException

    @Message(code = 333, value = "hello %s")
    fun basicArgs(world: String): String
}
