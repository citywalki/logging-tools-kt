package pro.walkin.logging.processor.bundle

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import pro.walkin.logging.processor.toCamelCase
import kotlin.collections.ifEmpty

internal data class BundleInterfaceMessageFunctionSource(
    private val declaration: KSFunctionDeclaration,
    val name: String,
    val value: String
){
    fun parameters() = declaration.parameters

    fun returnType() = declaration.returnType
}

internal data class BundleInterfaceDefinitionSource(
    val packageName: String,
    val projectCode: String,
    val aliases: List<String>,
    val interfaceDeclaration: KSClassDeclaration,
    val interfaceSimpleName: String,
    val messageFunctions: Set<BundleInterfaceMessageFunctionSource>
)

internal val BundleInterfaceDefinitionSource.names: List<String>
    get() = this.aliases.ifEmpty {
        val alias = toCamelCase(this.interfaceDeclaration.simpleName.asString())
        listOf(alias)
    }

internal data class MessageBundle(
    val interfaceName: String,
    val importStatements : Set<String>,
    val messageMethods: List<MessageMethod>,
)

internal data class MessageMethod(
    val name: String,
    val parameters: List<Parameter>,
    val returnType: ReturnType,
    val translations: Map<String, String>,
    val defaultMessage: String,
)

internal data class ReturnType(
    val name: String,
    val isThrowable: Boolean,
)

internal data class Parameter(
    val name: String,
    val declaration: KSValueParameter,
    val isArray: Boolean,
    val isPrimitive: Boolean,
    val isVarArgs: Boolean,
    val objectType: String,
)
