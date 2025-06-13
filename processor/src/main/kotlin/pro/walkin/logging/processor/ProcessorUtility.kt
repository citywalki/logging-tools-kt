package pro.walkin.logging.processor

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Nullability
import java.nio.CharBuffer
import kotlin.reflect.KClass

internal fun KSAnnotation.findValue(name: String): Any? {
    return this.arguments
        .filter { it.name?.asString() == name }
        .map { it.value }
        .firstOrNull()
}

internal fun KSAnnotated.findAnnotations(klass: KClass<*>): List<KSAnnotation> {
    return this.annotations.filter { it.shortName.asString() == klass.simpleName }.toList()
}

internal fun KSAnnotated.findAnnotation(klass: KClass<*>): KSAnnotation? {
    return this.annotations.firstOrNull { it.shortName.asString() == klass.simpleName }
}

internal fun KSAnnotated.findAnnotation(simpleName: String): KSAnnotation? {
    return this.annotations.firstOrNull { it.shortName.asString() == simpleName }
}

internal fun KSAnnotated.hasAnnotation(klass: KClass<*>): Boolean {
    return findAnnotation(klass) != null
}

internal class TypeArgumentResolver(
    private val parent: TypeArgumentResolver? = null,
    typeParameters: List<KSTypeParameter> = emptyList(),
    typeArguments: List<KSTypeArgument> = emptyList(),
) {
    private val context = typeParameters.map { it.name.asString() }.zip(typeArguments).toMap()

    fun resolve(declaration: KSDeclaration): KSTypeArgument? {
        val typeArgument = context[declaration.simpleName.asString()]
        return if (typeArgument != null) {
            if (parent != null) {
                val typeParameter = typeArgument.type?.resolve()?.declaration as? KSTypeParameter
                if (typeParameter != null) {
                    parent.resolve(typeParameter) ?: typeArgument
                } else {
                    typeArgument
                }
            } else {
                typeArgument
            }
        } else {
            null
        }
    }
}

internal val KSType.name: String
    get() {
        fun asString(): String {
            return (declaration.qualifiedName ?: declaration.simpleName).asString()
        }

        return createTypeName(this, asString())
    }

internal val KSType.backquotedName: String
    get() {
        fun asString(): String {
            return createBackquotedName(declaration)
        }

        return createTypeName(this, asString())
    }

internal fun createBackquotedName(declaration: KSDeclaration): String {
    val qualifiedName = declaration.qualifiedName?.asString()
    return if (qualifiedName == null) {
        declaration.simpleName.asString()
    } else {
        val packageName = declaration.packageName.asString()
        if (packageName.isEmpty()) {
            qualifiedName
        } else {
            val remains = qualifiedName.substring(packageName.length)
            packageName.split('.').joinToString(".") { "`$it`" } + remains
        }
    }
}

private fun createTypeName(type: KSType, baseName: String): String {
    val buf = StringBuilder()
    buf.append(baseName)
    if (type.arguments.isNotEmpty()) {
        buf.append("<")
        type.arguments.joinTo(buf) {
            val t = it.type?.resolve()
            if (t == null) {
                it.variance.label
            } else {
                val mark = if (t.nullability == Nullability.NULLABLE) "?" else ""
                t.name + mark
            }
        }
        buf.append(">")
    }
    return buf.toString()
}

internal fun toCamelCase(text: String): String {
    val builder = StringBuilder()
    val buf = CharBuffer.wrap(text)
    if (buf.hasRemaining()) {
        builder.append(buf.get().lowercaseChar())
    }
    while (buf.hasRemaining()) {
        val c1 = buf.get()
        buf.mark()
        if (buf.hasRemaining()) {
            val c2 = buf.get()
            if (c1.isUpperCase() && c2.isLowerCase()) {
                builder.append(c1).append(c2).append(buf)
                break
            } else {
                builder.append(c1.lowercaseChar())
                buf.reset()
            }
        } else {
            builder.append(c1.lowercaseChar())
        }
    }
    return builder.toString()
}
