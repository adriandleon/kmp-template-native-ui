import com.adarshr.gradle.testlogger.theme.ThemeType
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.ncorti.ktfmt.gradle.TrailingCommaManagementStrategy.COMPLETE
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotest)
    alias(libs.plugins.ktfmt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.test.logger)
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
            implementation(libs.crashkios.crashlytics)
            implementation(libs.essenty.lifecycle.coroutines)
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.config)
            implementation(libs.firebase.crashlytics)
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
    trailingCommaManagementStrategy = COMPLETE
}

detekt {
    parallel = true
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/config/detekt.yml")
}

kover.reports {
    verify.rule { minBound(90) }

    filters.excludes {
        classes("com.adriandeleon.template.common.util.AndroidDispatcher")
        files("*.*Module.*", "*.di.*Module*", "*.di.*")
    }
}

testlogger {
    theme = ThemeType.MOCHA
    showFullStackTraces = false
    slowThreshold = 2000
    showSummary = true
    showPassed = true
    showSkipped = true
}

buildkonfig {
    packageName = "com.adriandeleon.template"

    // DefaultConfig
    defaultConfigs {
        buildConfigField(
            type = BOOLEAN,
            name = "DEBUG",
            value = "true",
            nullable = false,
            const = true,
        )

        buildConfigField(
            type = STRING,
            name = "SUPABASE_KEY",
            value = getSecret("SUPABASE_KEY_DEV"),
            nullable = false,
            const = true,
        )

        buildConfigField(
            type = STRING,
            name = "SUPABASE_URL",
            value = getSecret("SUPABASE_URL_DEV_AND"),
            nullable = false,
            const = true,
        )

        buildConfigField(
            type = STRING,
            name = "CONFIGCAT_KEY",
            value = getSecret("CONFIGCAT_AND_TEST_KEY"),
            nullable = false,
            const = true,
        )
    }

    // Flavored DefaultConfig
    defaultConfigs("release") {
        buildConfigField(
            type = BOOLEAN,
            name = "DEBUG",
            value = "false",
            nullable = false,
            const = true,
        )

        buildConfigField(
            type = STRING,
            name = "SUPABASE_KEY",
            value = getSecret("SUPABASE_KEY_PROD"),
            nullable = false,
            const = true,
        )

        buildConfigField(
            type = STRING,
            name = "SUPABASE_URL",
            value = getSecret("SUPABASE_URL_PROD"),
            nullable = false,
            const = true,
        )

        buildConfigField(
            type = STRING,
            name = "CONFIGCAT_KEY",
            value = getSecret("CONFIGCAT_AND_LIVE_KEY"),
            nullable = false,
            const = true,
        )
    }

    // TargetConfig
    targetConfigs {
        create("iosArm64") {
            buildConfigField(
                type = STRING,
                name = "SUPABASE_URL",
                value = getSecret("SUPABASE_URL_DEV_IOS"),
                nullable = false,
                const = true,
            )

            buildConfigField(
                type = STRING,
                name = "CONFIGCAT_KEY",
                value = getSecret("CONFIGCAT_IOS_TEST_KEY"),
                nullable = false,
                const = true,
            )
        }

        create("iosSimulatorArm64") {
            buildConfigField(
                type = STRING,
                name = "SUPABASE_URL",
                value = getSecret("SUPABASE_URL_DEV_IOS"),
                nullable = false,
                const = true,
            )

            buildConfigField(
                type = STRING,
                name = "CONFIGCAT_KEY",
                value = getSecret("CONFIGCAT_IOS_TEST_KEY"),
                nullable = false,
                const = true,
            )
        }
    }

    // Flavored TargetConfig
    targetConfigs("release") {
        create("iosArm64") {
            buildConfigField(
                type = STRING,
                name = "SUPABASE_URL",
                value = getSecret("SUPABASE_URL_PROD"),
                nullable = false,
                const = true,
            )

            buildConfigField(
                type = STRING,
                name = "CONFIGCAT_KEY",
                value = getSecret("CONFIGCAT_IOS_LIVE_KEY"),
                nullable = false,
                const = true,
            )
        }

        create("iosSimulatorArm64") {
            buildConfigField(
                type = STRING,
                name = "SUPABASE_URL",
                value = getSecret("SUPABASE_URL_PROD"),
                nullable = false,
                const = true,
            )

            buildConfigField(
                type = STRING,
                name = "CONFIGCAT_KEY",
                value = getSecret("CONFIGCAT_IOS_LIVE_KEY"),
                nullable = false,
                const = true,
            )
        }
    }
}

fun getSecret(key: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(key)
        ?: System.getenv(key)
        ?: throw InvalidUserDataException(
            "Missing secret $key in local.properties or environment variables"
        )
}
