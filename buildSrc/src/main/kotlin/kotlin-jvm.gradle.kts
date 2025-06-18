package buildsrc.convention

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin in JVM projects.
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
}

val versionCatalog = versionCatalogs.named("libs")
val javaToolchainVersion = versionCatalog.findVersion("java-compile-toolchain").get().requiredVersion
val jvmTargetVersion = versionCatalog.findVersion("jvm-target").get().requiredVersion

kotlin {
    jvmToolchain(javaToolchainVersion.toInt())
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
    baseline = rootProject.file("config/detekt/baseline.xml")
}
tasks.withType<Detekt>().configureEach {
    jvmTarget = jvmTargetVersion
    reports {
        xml.required = true
        html.required = true
        sarif.required = true
        md.required = true
    }
    basePath = rootProject.rootDir.path
}
//detektReportMergeSarif {
//    input.from(tasks.withType<Detekt>().map { it.reports.sarif.outputLocation })
//}

tasks.withType<Test>().configureEach {
    // Configure all test Gradle tasks to use JUnitPlatform.
    useJUnitPlatform()

    // Log information about all test results, not only the failed ones.
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
    }
}
