import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ktfmt.gradle)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.lumo)
}

kotlin {
    androidTarget { compilerOptions.jvmTarget.set(JvmTarget.JVM_17) }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.splashscreen)
        }
        commonMain.dependencies {
            implementation(projects.shared)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.decompose)
            implementation(libs.decompose.extensions)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.okhttp)
            implementation(libs.koin.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.lifecycle.viewmodel.compose)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
    }
}

android {
    namespace = "com.adriandeleon.template"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.adriandeleon.template"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    buildTypes { getByName("release") { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

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
}

dependencies {
    debugImplementation(compose.uiTooling)
    detektPlugins(libs.detekt.compose)
}
