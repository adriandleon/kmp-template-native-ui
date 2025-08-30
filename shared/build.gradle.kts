import org.gradle.kotlin.dsl.detektPlugins
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotest)
    alias(libs.plugins.ktfmt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
}

kotlin {
    jvmToolchain(17)

    androidTarget { compilerOptions.jvmTarget.set(JvmTarget.JVM_17) }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(libs.decompose)
            export(libs.essenty.lifecycle)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.decompose)
            api(libs.essenty.lifecycle)
            api(libs.koin.core)
            implementation(libs.configcat)
            implementation(libs.datastore.preferences)
            implementation(libs.decompose.extensions)
            implementation(libs.essenty.lifecycle.coroutines)
            implementation(libs.kermit)
            implementation(libs.kermit.crashlytics)
            implementation(libs.kermit.koin)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization)
            implementation(libs.mvikotlin.coroutines)
            implementation(libs.mvikotlin.core)
            implementation(libs.mvikotlin.logging)
            implementation(libs.mvikotlin.main)
            implementation(libs.mvikotlin.timetravel)
            implementation(libs.slf4j.nop)
            implementation(libs.supabase.postgrest)
        }

        androidMain.dependencies {
            api(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies { implementation(libs.ktor.client.darwin) }

        commonTest.dependencies {
            implementation(libs.kermit.test)
            implementation(libs.koin.test)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.extensions.koin)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.property)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

android {
    namespace = "com.adriandeleon.template.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }

    testOptions.unitTests.all { it.useJUnitPlatform() }
}

dependencies { detektPlugins(libs.detekt.compose) }

ktfmt {
    kotlinLangStyle()
    removeUnusedImports = true
    manageTrailingCommas = true
}

detekt {
    parallel = true
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt.yml")
}
