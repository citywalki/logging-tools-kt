import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.ksp.symbol.processing.api)
    implementation(project(":core"))
    ksp(project(":processor"))

    testImplementation(libs.kotlin.test)
}
ksp {
    arg("translationFilesPath", "${project.projectDir}/messages/")
}
