package pro.walkin.logging.processor.bundle

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.symbol.KSType
import pro.walkin.logging.processor.Context
import pro.walkin.logging.processor.bundle.MessageBundleFactory.Companion.GENERATED_FILE_EXTENSION
import pro.walkin.logging.processor.bundle.MessageBundleFactory.Companion.TRANSLATION_FILE_EXTENSION_PATTERN
import pro.walkin.logging.processor.name
import java.io.File
import java.io.FilenameFilter
import java.util.*
import java.util.regex.Pattern

internal class MessageBundleFactory(
    private val context: Context,
    private val definitionSource: BundleInterfaceDefinitionSource
) {

    companion object {
        const val GENERATED_FILE_EXTENSION = ".i18n_locale_COUNTRY_VARIANT.properties"
        const val TRANSLATION_FILE_EXTENSION_PATTERN = ".i18n_[a-z]*(_[A-Z]*){0,2}\\.properties"
    }

    fun create(): MessageBundle {
        val interfaceName = definitionSource.interfaceDeclaration.simpleName.asString()

        val (importStatements, messageMethods) = messageMethods()

        return MessageBundle(
            messageMethods = messageMethods,
            importStatements = importStatements,
            interfaceName = interfaceName
        )
    }

    private fun messageMethods(): Pair<MutableSet<String>, List<MessageMethod>> {

        // find interface all translation files
        val interfaceTranslations = interfaceTranslations()

        val importStates = mutableSetOf<String>()

        val messageMethods = definitionSource.messageFunctions.map { functionSource ->

            val translations = interfaceTranslations.getOrDefault(functionSource.name, mapOf())

            val parameters = functionSource.parameters().map { parameterSource ->
                val parameterTypeRef = parameterSource.type
                val parameterType = parameterTypeRef.resolve()

                addImportStatements(importStates, parameterType)

                Parameter(
                    name = parameterSource.name!!.asString(),
                    declaration = parameterSource,
                    isArray = false,
                    isPrimitive = false,
                    isVarArgs = parameterSource.isVararg,
                    objectType = parameterTypeRef.toString()
                )
            }

            val returnTypeRef = functionSource.returnType()
            val returnType = returnTypeRef!!.resolve()

            addImportStatements(importStates, returnType)

            val kotlinThrowable = context.resolver.getClassDeclarationByName<Throwable>()?.asType(emptyList())
            val isKotlinException = kotlinThrowable?.isAssignableFrom(returnType)
//            val javaThrowable = context.resolver.getClassDeclarationByName("java.lang.Exception")?.asType(emptyList())
//            val isJavaException = javaThrowable?.isAssignableFrom(aa)

            val returnTypeModel = ReturnType(
                name = returnType.declaration.simpleName.asString(),
                isThrowable = isKotlinException == true,
            )

            MessageMethod(
                name = functionSource.name,
                parameters = parameters,
                returnType = returnTypeModel,
                translations = translations,
                defaultMessage = functionSource.value,
            )
        }

        return importStates to messageMethods
    }

    private fun addImportStatements(importStatements: MutableSet<String>, importClassType: KSType) {
        val importClass = importClassType.name
        if (importClass.startsWith("kotlin.")) {
            return
        }
        importStatements.add(importClassType.name)
    }

//    fun isTypeThrowableSubtype(typeRef: KSTypeReference): Boolean {
//        // 1. 解析类型引用获取实际类型
//        val type = typeRef.resolve()
//
//        // 2. 获取 Throwable 的类型声明
//        val throwableType = context.resolver.getClassDeclarationByName(Throwable::class.simpleName.toString())
//
//        // 3. 检查类型是否是 Throwable 的子类型
//        return type.
//    }

    /**
     *
     */
    private fun interfaceTranslations(): Map<String, Map<String, String>> {
        var methodLocaleMessages = mapOf<String, Map<String, String>>()
        val translationFiles = findTranslationFiles()
        if (translationFiles.isNotEmpty()) {
            translationFiles.forEach { translationFile ->
                val translationSuffix = getTranslationClassNameSuffix(translationFile.name)
                // The locale should be the same as the translationsSuffix minus the leading _
                val locale = translationSuffix.substring(1)

                val properties = Properties().apply {
                    load(translationFile.inputStream().reader())
                }
                properties.forEach { (key, value) ->
                    val translations = methodLocaleMessages.getOrDefault(key as String, mutableMapOf())

                    methodLocaleMessages =
                        methodLocaleMessages.plus(key to translations.plus(locale to value.toString()))
                }

            }
        }
        return methodLocaleMessages.toMap()
    }

    private fun getTranslationClassNameSuffix(translationFileName: String): String {
        val pattern = Pattern.compile("[^_]*((_[^_.]*){1,3}).*")
        val matcher = pattern.matcher(translationFileName)
        val found = matcher.find()

        if (!found) {
            throw IllegalArgumentException("The given filename is not a valid property filename")
        }

        return matcher.group(1)!!
    }

    fun findTranslationFiles(): List<File> {
        val packageName = definitionSource.packageName
        val interfaceName = definitionSource.interfaceSimpleName
        val translationFilesPath = context.config.translationFilesPath


        val classTranslationFilesPath = if (translationFilesPath != null) {
            translationFilesPath + packageName.replace('.', File.separatorChar)
        } else {

//            throw RuntimeException("translationFilesPath is null")
            packageName.replace('.', File.separatorChar)
        }

        val files: Array<File?> = File(classTranslationFilesPath)
            .listFiles(TranslationFileFilter(interfaceName))
        if (files.isEmpty()) {
            return listOf()
        } else {
            val result = files.filterNotNull()
//            result = Arrays.asList<File?>(*files)
//            Collections.sort<File?>(result, object : Comparator<File?> {
//                override fun compare(o1: File, o2: File): Int {
//                    var result = o1.getAbsolutePath().compareTo(o2.getAbsolutePath())
//                    result = (if (result != 0) result else Integer.signum(o1.getName().length - o2.getName().length))
//                    return result
//                }
//            })
            return result
        }

    }
}

private class TranslationFileFilter(private val className: String) : FilenameFilter {
    override fun accept(dir: File?, name: String): Boolean {
        val isGenerated: Boolean = name.endsWith(GENERATED_FILE_EXTENSION)
        val isTranslationFile = name.matches((Pattern.quote(className) + TRANSLATION_FILE_EXTENSION_PATTERN).toRegex())

        return !isGenerated && isTranslationFile
    }
}
