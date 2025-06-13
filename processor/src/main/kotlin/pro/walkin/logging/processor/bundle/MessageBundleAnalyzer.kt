package pro.walkin.logging.processor.bundle

import com.google.devtools.ksp.symbol.KSAnnotated
import pro.walkin.logging.processor.Context
import pro.walkin.logging.processor.Exit

internal class MessageBundleAnalyzer(
    private val context: Context,
    private val definitionSourceResolver: BundleInterfaceDefinitionSourceResolver,
) {
    fun analyze(symbol: KSAnnotated): MessageBundleAnalysisResult {
        val definitionSource = try {
            definitionSourceResolver.resolve(symbol)
        } catch (e: Exit) {
            return MessageBundleAnalysisResult.Error(e)
        }
        return if (definitionSource == null) {
            MessageBundleAnalysisResult.Skip
        } else {
            try {
                val bundle = MessageBundleFactory(context,definitionSource).create()
                val model = MessageBundleModel(definitionSource, bundle)
                MessageBundleAnalysisResult.Success(model)
            } catch (e: Exit) {
                val model = MessageBundleModel(definitionSource)
                MessageBundleAnalysisResult.Failure(model, e)
            }
        }
    }

}


internal sealed class MessageBundleAnalysisResult {
    data class Success(val model: MessageBundleModel) : MessageBundleAnalysisResult()
    data class Failure(val model: MessageBundleModel, val exit: Exit) : MessageBundleAnalysisResult()
    data class Error(val exit: Exit) : MessageBundleAnalysisResult()
    object Skip : MessageBundleAnalysisResult()
}
