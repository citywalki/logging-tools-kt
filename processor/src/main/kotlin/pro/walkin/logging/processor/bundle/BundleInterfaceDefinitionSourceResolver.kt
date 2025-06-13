package pro.walkin.logging.processor.bundle

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import pro.walkin.logging.annotations.Message
import pro.walkin.logging.annotations.MessageBundle
import pro.walkin.logging.processor.Context
import pro.walkin.logging.processor.findAnnotation
import pro.walkin.logging.processor.findAnnotations
import pro.walkin.logging.processor.findValue
import pro.walkin.logging.processor.report

internal class BundleInterfaceDefinitionSourceResolver(private val context: Context) {
    fun resolve(symbol: KSAnnotated): BundleInterfaceDefinitionSource{
        val defDeclaration = symbol as? KSClassDeclaration ?: report("is null", symbol)

        val annotation = defDeclaration.findAnnotation(MessageBundle::class)
        val projectCode = annotation?.findValue("projectCode") ?: report("projectCode is null", symbol)
        val aliases = annotation?.findValue("aliases")

        if (aliases !is List<*>) {
            report("The aliases value of @${MessageBundle::class.simpleName} is invalid.", defDeclaration)
        }

        val messageFunctions = mutableSetOf<BundleInterfaceMessageFunctionSource>()
        defDeclaration.getAllFunctions().forEach { method ->
          val messageAnnotation =  method.findAnnotation(Message::class)
            if (messageAnnotation != null) {
                val functionSource = BundleInterfaceMessageFunctionSource(
                    method,
                    name = method.simpleName.asString(),
                    value = messageAnnotation.findValue("value").toString()
                )
                messageFunctions.add(functionSource)
            }
        }

        return BundleInterfaceDefinitionSource(
            interfaceDeclaration = defDeclaration,
            packageName = defDeclaration.packageName.asString(),
            projectCode = projectCode.toString(),
            interfaceSimpleName = defDeclaration.simpleName.asString(),
            messageFunctions = messageFunctions,
            aliases = aliases.map { it.toString() }
        )
    }
}
