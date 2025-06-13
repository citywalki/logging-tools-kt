import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.ksp.symbol.processing.api)
    implementation(project(":core"))
    testImplementation(project(":core"))
    testImplementation(libs.ksp.symbol.processing)
    testImplementation(libs.ksp.test)
    testImplementation(libs.kotlin.test)
}

tasks {
    withType<KotlinCompile>().configureEach {
        compilerOptions {
            if (name == "compileTestKotlin") {
                optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
            }
        }
    }
}
