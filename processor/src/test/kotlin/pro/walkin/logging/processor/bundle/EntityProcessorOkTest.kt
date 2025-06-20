package pro.walkin.logging.processor.bundle

import org.junit.jupiter.api.Tag
import pro.walkin.logging.processor.AbstractKspTest

@Tag("slow")
class EntityProcessorOkTest : AbstractKspTest(MessageBundleProcessorProvider()) {

    fun `basic test`() {
        val result = compile(
            """
            package tests.messages
            import pro.walkin.logging.annotations.*
            @MessageBundle(projectCode = "test")
            interface BasicMessages{
                @Message("1111")
                fun basic(): String
            }
            """
        )

        result.generatedFiles.first()
    }
}
