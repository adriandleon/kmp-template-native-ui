import com.ncorti.ktfmt.gradle.TrailingCommaManagementStrategy.COMPLETE
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ktfmt.gradle)
    alias(libs.plugins.lumo)
}

kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }

android {
    namespace = "com.adriandeleon.kmp.template"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.adriandeleon.kmp.template"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging.resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
        excludes += "/META-INF/licenses/ASM"
        excludes += "/META-INF/LICENSE.md"
        excludes += "/META-INF/LICENSE-notice.md"
        pickFirsts += "win32-x86-64/attach_hotspot_windows.dll"
        pickFirsts += "win32-x86/attach_hotspot_windows.dll"
    }

    buildTypes {
        getByName("debug") {
            isShrinkResources = false
            isMinifyEnabled = false
            isDebuggable = true
            testProguardFiles("proguard-test-rules.pro")
        }

        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = false
            ndk.debugSymbolLevel = "FULL"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
        )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

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

dependencies {
    implementation(projects.shared)
    
    implementation(libs.androidx.activity.compose)
    implementation(libs.splashscreen)
    implementation(libs.compose.components.resources)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.decompose)
    implementation(libs.decompose.extensions)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.koin.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    
    testImplementation(libs.kotlin.test)
    
    debugImplementation(libs.compose.ui.tooling)
    detektPlugins(libs.detekt.compose)
}
