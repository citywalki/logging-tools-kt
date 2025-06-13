package pro.walkin.logging.processor.bundle

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import pro.walkin.logging.annotations.MessageBundle
import pro.walkin.logging.processor.Context
import pro.walkin.logging.processor.ContextFactory
import pro.walkin.logging.processor.Exit
import java.io.PrintWriter

internal class MessageBundleProcessor(
    private val contextFactory: ContextFactory,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val context = contextFactory.create(resolver)
        val processedSymbols = mutableSetOf<KSAnnotated>()
        val annotationName = MessageBundle::class.qualifiedName!!
        val symbols = resolver.getSymbolsWithAnnotation(annotationName)
        val analyzer = MessageBundleAnalyzer(
            context,
            BundleInterfaceDefinitionSourceResolver(context),
        )

        for (symbol in (symbols - processedSymbols)) {
            val model = when (val result = analyzer.analyze(symbol)) {
                is MessageBundleAnalysisResult.Success -> result.model
                is MessageBundleAnalysisResult.Failure -> {
                    log(context, result.exit)
                    result.model
                }

                is MessageBundleAnalysisResult.Error -> {
                    log(context, result.exit)
                    continue
                }

                is MessageBundleAnalysisResult.Skip -> {
                    continue
                }
            }
            generateMessageImpl(context, model)
        }
        processedSymbols.addAll(symbols)

        return emptyList()
    }

    private fun log(context: Context, exit: Exit) {
        context.logger.error(exit.report.message, exit.report.node)
    }

    private fun generateMessageImpl(context: Context, model: MessageBundleModel) {
        val dependencies = Dependencies(false, *model.containingFiles.toTypedArray())
        val (packageName, simpleName) = model.createMessageBundleClassName()
        context.codeGenerator.createNewFile(dependencies,packageName,simpleName).use { out ->
            PrintWriter(out).use { writer ->

                MessageBundleGenerator(
                    context,
                    model.bundle!!,
                    packageName,
                    model.aliases,
                    simpleName,
                    writer
                ).run()
            }
        }
    }
}
