package pro.walkin.logging.processor.bundle

import pro.walkin.logging.processor.Context
import java.io.PrintWriter
import java.time.ZonedDateTime

internal class MessageBundleGenerator(
    @Suppress("UnusedPrivateProperty")
    private val context: Context,
    private val bundle: MessageBundle,
    private val packageName: String,
    private val aliases: List<String>,
    private val simpleName: String,
    private val w: PrintWriter,
) : Runnable {
    override fun run() {
        packageDeclaration()
        importStatements()
        classDeclaration()

        i18nMessages()
    }

    private fun i18nMessages() {
        for (alias in aliases) {
            w.println("public val I18nMessages.`$alias`: ${bundle.interfaceName} get() = $simpleName.`$alias`")
        }
        w.println()
    }

    private fun packageDeclaration() {
        if (packageName.isNotEmpty()) {
            w.println("package $packageName")
            w.println()
        }
    }

    private fun importStatements() {
        w.println("import pro.walkin.logging.I18nMessages")
        w.println("import pro.walkin.logging.spi.LocaleLoader")
        w.println("import pro.walkin.logging.I18nUtils")
        w.println("import java.util.*")

        bundle.importStatements.forEach {
            w.println("import $it")
        }
        w.println()
    }

    private fun classDeclaration() {
        w.println("// generated at ${ZonedDateTime.now()}")
        w.println(
            "public class $simpleName : ${bundle.interfaceName} {"
        )
        w.println()

        // getLoggingLocale
        w.println(
            """
          fun getLoggingLocale(): Locale {
              val loader = ServiceLoader.load(LocaleLoader::class.java)
              val localeLoader = loader.firstOrNull()
              return localeLoader?.getLocale() ?: return Locale.getDefault()
          }
        """.replaceIndent("  ")
        )

        bundle.messageMethods.forEach {
            messageMethod(it)
        }

        companionObject()

        w.println("}")
        w.println()
    }

    private fun companionObject() {
        w.println("    companion object {")
        for (alias in aliases) {
            w.println("        public val `$alias`: $simpleName = $simpleName()")
        }
        w.println("    }")
    }

    private fun methodParameters(parameters: List<Parameter>): String {
        return parameters.joinToString(",") { parameter -> "${parameter.name} : ${parameter.objectType}" }
    }

    private fun messageMethod(method: MessageMethod) {
        w.println(
            "  override fun ${method.name}(${methodParameters(method.parameters)}) : ${method.returnType.name} {"
        )
        w.println("    val locale = getLoggingLocale()")
        w.println()

        val defaultMessage = "\"${method.defaultMessage}\""
        val translationMessage = method.translations.map { (key, value) ->
            """
                if(I18nUtils.localeEquals(locale, "$key")) {
                    "$value"
                }
            """.trimIndent()
        }.let { messages ->
            if (messages.isNotEmpty()) {
                messages.plus(" { $defaultMessage }")
            } else {
                listOf(defaultMessage)
            }
        }.joinToString(" else ")

        w.println("var message = $translationMessage".replaceIndent("    "))

        val parameterStr = method.parameters.joinToString(",") { parameter ->
            parameter.name
        }
        w.println("    message = message.format(locale${if (parameterStr.isBlank()) "" else ", $parameterStr"})")

        w.println()
        if (method.returnType.isThrowable) {
            w.println("    val exception = ${method.returnType.name}(message)")
            w.println("    val st = exception.stackTrace")
            w.println("    exception.stackTrace = st.copyOfRange(1, st.size)")
            w.println("    return exception")
        } else {
            w.println("    return message")
        }

        w.println("  }")
        w.println()
    }
}
