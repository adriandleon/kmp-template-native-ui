import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.withType

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotest) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.ksp) apply false
}

subprojects {
    tasks.register("detektAll") {
        allprojects { this@register.dependsOn(tasks.withType<Detekt>()) }
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "17"
    }
}
