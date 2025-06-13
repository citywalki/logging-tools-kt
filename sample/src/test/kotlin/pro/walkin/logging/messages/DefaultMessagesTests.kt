package pro.walkin.logging.messages

import pro.walkin.logging.I18nMessages
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DefaultMessagesTests {

    @Test
    fun `test default message`() {
        assertEquals("基础1", I18nMessages.defaultGeneratorMessages.basic())
        assertEquals("hello you", I18nMessages.defaultGeneratorMessages.basicArgs("you"))
        assertNotEquals("hello you1", I18nMessages.defaultGeneratorMessages.basicArgs("you"))

    }
}
