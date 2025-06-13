// The code in this file is a convention plugin - a Gradle mechanism for sharing reusable build logic.
// `buildSrc` is a Gradle-recognized directory and every plugin there will be easily available in the rest of the build.
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

val jvmTargetVersion = versionCatalog.findVersion("jvm-target").get().requiredVersion

kotlin {
    // Use a specific Java version to make it easier to work in different environments.
    jvmToolchain(JavaLanguageVersion.of(versionCatalog.findVersion("java-compile-toolchain").get().requiredVersion).asInt())
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
    jvmTarget = "21"
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
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "21"
}

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
