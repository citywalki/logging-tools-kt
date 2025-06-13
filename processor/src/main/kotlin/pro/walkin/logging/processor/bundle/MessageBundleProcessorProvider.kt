package pro.walkin.logging.processor.bundle

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import pro.walkin.logging.processor.Config
import pro.walkin.logging.processor.Context
import pro.walkin.logging.processor.ContextFactory

class MessageBundleProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val config = Config.create(environment.options)
        val factory = ContextFactory { resolver -> Context(environment, config, resolver) }

        return MessageBundleProcessor(factory)
    }
}
