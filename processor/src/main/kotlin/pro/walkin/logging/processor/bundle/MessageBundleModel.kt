package pro.walkin.logging.processor.bundle

import com.google.devtools.ksp.symbol.KSFile

internal class MessageBundleModel(
    private val definitionSource: BundleInterfaceDefinitionSource,
    val bundle: MessageBundle? = null,
) {

    val aliases = definitionSource.names

    val containingFiles: List<KSFile>
        get() {
            val defDeclaration = definitionSource.interfaceDeclaration
            return listOfNotNull(defDeclaration.containingFile)
        }

    fun createMessageBundleClassName(): Pair<String, String> {

        val messageBundleClassName = definitionSource.interfaceSimpleName + "Bundle"
        return definitionSource.packageName to messageBundleClassName
    }
}
