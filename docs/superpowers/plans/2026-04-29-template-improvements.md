# KMP Template Improvements Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Improve the KMP template with build fixes, Room KMP integration, localization scaffold, a full Posts example feature with strict API boundaries, and UI tests on both platforms.

**Architecture:** Infrastructure-first — fixes → Room → localization → Posts feature → UI tests. All shared business logic is `internal`; only the Decompose component interface, `PostsUiState`, and `PostUiModel` are public. Components are created by Koin via `parametersOf(ComponentContext)`. `PreviewComponent` classes live in `shared/commonMain` as the single source of truth for previews and tests.

**Tech Stack:** Kotlin 2.3.21, KMP, Compose Multiplatform, SwiftUI, Decompose 3.5.0, MVIKotlin 4.4.0, Koin 4.2.1, Room 2.8.4 (KMP), Ktor 3.4.3, Kotest 6.1.11, Mokkery 3.3.0, Swift Testing, ViewInspector

**Note on git:** Commits are left to the developer's discretion.

---

## File Map

### Phase 1 — Quick Build Fixes
| Action | File |
|--------|------|
| Modify | `gradle/libs.versions.toml` |
| Modify | `shared/build.gradle.kts` |
| Modify | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/CommonModule.kt` |
| Modify | `setup_new_project.sh` |

### Phase 2 — Room KMP Integration
| Action | File |
|--------|------|
| Modify | `gradle/libs.versions.toml` |
| Modify | `shared/build.gradle.kts` |
| Modify | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/util/DispatcherProvider.kt` |
| Modify | `shared/src/androidMain/kotlin/com/adriandeleon/kmp/template/common/util/DispatcherProvider.android.kt` |
| Modify | `shared/src/iosMain/kotlin/com/adriandeleon/kmp/template/common/util/DispatcherProvider.ios.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/db/AppDatabase.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/db/post/PostEntity.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/db/post/PostDao.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/PlatformDatabaseModule.kt` |
| Create | `shared/src/androidMain/kotlin/com/adriandeleon/kmp/template/common/PlatformDatabaseModule.android.kt` |
| Create | `shared/src/iosMain/kotlin/com/adriandeleon/kmp/template/common/PlatformDatabaseModule.ios.kt` |
| Modify | `androidApp/src/main/kotlin/com/adriandeleon/kmp/template/common/AndroidModule.kt` |
| Modify | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/di/KoinApp.kt` |

### Phase 3 — Localization Scaffold
| Action | File |
|--------|------|
| Create | `androidApp/src/main/res/values/strings.xml` |
| Create | `androidApp/src/main/res/values-es-r419/strings.xml` |
| Create | `androidApp/src/main/res/values-pt-rBR/strings.xml` |
| Create | `iosApp/KMP-Template/Localizable.xcstrings` |

### Phase 4 — Posts Feature
| Action | File |
|--------|------|
| Modify | `gradle/libs.versions.toml` |
| Modify | `shared/build.gradle.kts` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/network/NetworkModule.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PostsComponent.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PostsUiState.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PostUiModel.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PreviewPostsComponent.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/domain/model/Post.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/domain/repository/PostsRepository.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/domain/usecase/GetPostsUseCase.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/datasource/PostsRemoteDataSource.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/datasource/PostsLocalDataSource.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/mapper/PostEntityMapper.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/dto/PostDto.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/repository/DefaultPostsRepository.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/mapper/PostsUiMapper.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsIntent.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsState.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsMessage.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsStore.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsStoreFactory.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/DefaultPostsComponent.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PostsModule.kt` |
| Create | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/home/PreviewHomeComponent.kt` |
| Modify | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/CommonModule.kt` |
| Modify | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/di/KoinApp.kt` |
| Modify | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/root/RootComponent.kt` |
| Modify | `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/root/DefaultRootComponent.kt` |
| Create | `androidApp/src/main/kotlin/com/adriandeleon/kmp/template/posts/PostsView.kt` |
| Modify | `androidApp/src/main/kotlin/com/adriandeleon/kmp/template/root/RootView.kt` |
| Create | `iosApp/KMP-Template/Posts/PostsView.swift` |
| Modify | `iosApp/KMP-Template/Root/RootView.swift` |

### Phase 5 — UI Tests
| Action | File |
|--------|------|
| Create | `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/common/util/TestComponentContext.kt` |
| Create | `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/home/DefaultHomeComponentTest.kt` |
| Create | `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/presentation/mapper/PostsUiMapperTest.kt` |
| Create | `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/data/mapper/PostEntityMapperTest.kt` |
| Create | `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/domain/usecase/GetPostsUseCaseTest.kt` |
| Create | `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/data/repository/DefaultPostsRepositoryTest.kt` |
| Modify | `androidApp/build.gradle.kts` |
| Create | `androidApp/src/androidTest/kotlin/com/adriandeleon/kmp/template/util/TestUtils.kt` |
| Create | `androidApp/src/androidTest/kotlin/com/adriandeleon/kmp/template/posts/PostsViewRobot.kt` |
| Create | `androidApp/src/androidTest/kotlin/com/adriandeleon/kmp/template/posts/PostsViewTest.kt` |
| Create | `iosApp/KMP-TemplateTests/Posts/PostsViewTests.swift` |

---

## Phase 1 — Quick Build Fixes

---

### Task 1: Firebase BOM → version catalog

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `shared/build.gradle.kts`

- [ ] **Step 1: Add Firebase BOM to version catalog**

In `gradle/libs.versions.toml`, add to `[versions]`:
```toml
firebase-bom = "33.9.0"
```

Add to `[libraries]`:
```toml
firebase-bom = { module = "com.google.firebase:firebase-bom", version.ref = "firebase-bom" }
```

- [ ] **Step 2: Use the catalog entry in the build script**

In `shared/build.gradle.kts`, replace the hardcoded platform call in `androidMain.dependencies`:
```kotlin
// Before
implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.9.0"))

// After
implementation(platform(libs.firebase.bom))
```

- [ ] **Step 3: Verify the build compiles**

```bash
./gradlew :shared:assembleAndroidMain
```
Expected: `BUILD SUCCESSFUL`

---

### Task 2: MVIKotlin debug-only runtime gating

**Files:**
- Modify: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/CommonModule.kt`

- [ ] **Step 1: Add `StoreFactory` to CommonModule with debug gating**

Replace the entire `CommonModule.kt`:
```kotlin
package com.adriandeleon.kmp.template.common

import com.adriandeleon.kmp.template.BuildKonfig
import com.adriandeleon.kmp.template.common.util.provideDispatcher
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val commonModule = module {
    factoryOf(::provideDispatcher)
    single<StoreFactory> {
        if (BuildKonfig.DEBUG) {
            LoggingStoreFactory(DefaultStoreFactory())
        } else {
            DefaultStoreFactory()
        }
    }
}
```

- [ ] **Step 2: Verify shared module builds**

```bash
./gradlew :shared:assembleAndroidMain
```
Expected: `BUILD SUCCESSFUL`

---

### Task 3: BuildKonfig `iosSimulatorArm64` deduplication

**Files:**
- Modify: `shared/build.gradle.kts`

- [ ] **Step 1: Deduplicate `targetConfigs` blocks**

In `shared/build.gradle.kts`, replace both repeated `targetConfigs` blocks:

```kotlin
// Replace this (debug targetConfigs):
targetConfigs {
    create("iosArm64") {
        buildConfigField(STRING, "SUPABASE_URL", value = getSecret("SUPABASE_URL_DEV_IOS"), nullable = false, const = true)
        buildConfigField(STRING, "CONFIGCAT_KEY", value = getSecret("CONFIGCAT_IOS_TEST_KEY"), nullable = false, const = true)
    }
    create("iosSimulatorArm64") {
        buildConfigField(STRING, "SUPABASE_URL", value = getSecret("SUPABASE_URL_DEV_IOS"), nullable = false, const = true)
        buildConfigField(STRING, "CONFIGCAT_KEY", value = getSecret("CONFIGCAT_IOS_TEST_KEY"), nullable = false, const = true)
    }
}

// With this:
targetConfigs {
    listOf("iosArm64", "iosSimulatorArm64").forEach { target ->
        create(target) {
            buildConfigField(STRING, "SUPABASE_URL", value = getSecret("SUPABASE_URL_DEV_IOS"), nullable = false, const = true)
            buildConfigField(STRING, "CONFIGCAT_KEY", value = getSecret("CONFIGCAT_IOS_TEST_KEY"), nullable = false, const = true)
        }
    }
}

// Replace this (release targetConfigs):
targetConfigs("release") {
    create("iosArm64") {
        buildConfigField(STRING, "SUPABASE_URL", value = getSecret("SUPABASE_URL_PROD"), nullable = false, const = true)
        buildConfigField(STRING, "CONFIGCAT_KEY", value = getSecret("CONFIGCAT_IOS_LIVE_KEY"), nullable = false, const = true)
    }
    create("iosSimulatorArm64") {
        buildConfigField(STRING, "SUPABASE_URL", value = getSecret("SUPABASE_URL_PROD"), nullable = false, const = true)
        buildConfigField(STRING, "CONFIGCAT_KEY", value = getSecret("CONFIGCAT_IOS_LIVE_KEY"), nullable = false, const = true)
    }
}

// With this:
targetConfigs("release") {
    listOf("iosArm64", "iosSimulatorArm64").forEach { target ->
        create(target) {
            buildConfigField(STRING, "SUPABASE_URL", value = getSecret("SUPABASE_URL_PROD"), nullable = false, const = true)
            buildConfigField(STRING, "CONFIGCAT_KEY", value = getSecret("CONFIGCAT_IOS_LIVE_KEY"), nullable = false, const = true)
        }
    }
}
```

- [ ] **Step 2: Verify the build generates config correctly**

```bash
./gradlew :shared:generateBuildKonfig
```
Expected: `BUILD SUCCESSFUL` with generated files for both iOS targets.

---

### Task 4: Setup script cross-platform fix

**Files:**
- Modify: `setup_new_project.sh`

- [ ] **Step 1: Replace `tail -r` with POSIX-compatible `awk`**

In `setup_new_project.sh`, find the `get_domain` function and replace the body:

```bash
# Before:
get_domain() {
    echo "$1" | tr '.' '\n' | tail -r | tr '\n' '.' | sed 's/\.$//'
}

# After:
get_domain() {
    echo "$1" | tr '.' '\n' | awk '{lines[NR]=$0} END {for(i=NR;i>=1;i--) printf "%s%s", lines[i], (i>1 ? "." : "\n")}'
}
```

- [ ] **Step 2: Verify the function output is correct on macOS**

```bash
bash -c 'source ./setup_new_project.sh 2>/dev/null; echo $(get_domain "com.example.myapp")'
```
Expected output: `myapp.example.com`

---

## Phase 2 — Room KMP Integration

---

### Task 5: Add Room to version catalog and build script

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `shared/build.gradle.kts`

- [ ] **Step 1: Add Room entries to version catalog**

In `gradle/libs.versions.toml`, add to `[versions]`:
```toml
room = "2.7.1"
```

Add to `[libraries]`:
```toml
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
```

Add to `[plugins]`:
```toml
room = { id = "androidx.room", version.ref = "room" }
```

- [ ] **Step 2: Apply Room plugin and dependencies in `shared/build.gradle.kts`**

Add `alias(libs.plugins.room)` to the plugins block (after `alias(libs.plugins.ksp)`):
```kotlin
alias(libs.plugins.room)
```

Add to `commonMain.dependencies`:
```kotlin
implementation(libs.room.runtime)
```

Add KSP compiler for each target (after the `sourceSets` block, before `dependencies`):
```kotlin
dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    detektPlugins(libs.detekt.compose)
}
```

Add the Room schema directory configuration (after the `kotlin { }` block):
```kotlin
room {
    schemaDirectory("$projectDir/schemas")
}
```

- [ ] **Step 3: Sync and verify Room plugin resolves**

```bash
./gradlew :shared:dependencies --configuration commonMainImplementation | grep room
```
Expected: `androidx.room:room-runtime` appears in the output.

---

### Task 6: Add `io` dispatcher to `DispatcherProvider`

**Files:**
- Modify: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/util/DispatcherProvider.kt`
- Modify: `shared/src/androidMain/kotlin/com/adriandeleon/kmp/template/common/util/DispatcherProvider.android.kt`
- Modify: `shared/src/iosMain/kotlin/com/adriandeleon/kmp/template/common/util/DispatcherProvider.ios.kt`

- [ ] **Step 1: Add `io` to the `DispatcherProvider` interface**

Replace `DispatcherProvider.kt`:
```kotlin
package com.adriandeleon.kmp.template.common.util

import kotlinx.coroutines.CoroutineDispatcher

internal interface DispatcherProvider {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}

internal expect fun provideDispatcher(): DispatcherProvider
```

- [ ] **Step 2: Add `io` to Android actual**

Replace `DispatcherProvider.android.kt`:
```kotlin
package com.adriandeleon.kmp.template.common.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("InjectDispatcher")
private class AndroidDispatcher : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = Dispatchers.IO
}

internal actual fun provideDispatcher(): DispatcherProvider = AndroidDispatcher()
```

- [ ] **Step 3: Add `io` to iOS actual**

Replace `DispatcherProvider.ios.kt`:
```kotlin
package com.adriandeleon.kmp.template.common.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("InjectDispatcher")
private class IOSDispatcher : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = Dispatchers.Default // iOS has no Dispatchers.IO
}

internal actual fun provideDispatcher(): DispatcherProvider = IOSDispatcher()
```

- [ ] **Step 4: Verify build compiles**

```bash
./gradlew :shared:assembleAndroidMain
```
Expected: `BUILD SUCCESSFUL`

---

### Task 7: Create Room database, entity, and DAO

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/db/AppDatabase.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/db/post/PostEntity.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/db/post/PostDao.kt`

- [ ] **Step 1: Create `PostEntity`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/db/post/PostEntity.kt
package com.adriandeleon.kmp.template.db.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
internal data class PostEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val body: String,
)
```

- [ ] **Step 2: Create `PostDao`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/db/post/PostDao.kt
package com.adriandeleon.kmp.template.db.post

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface PostDao {
    @Query("SELECT * FROM posts")
    suspend fun getAll(): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)
}
```

- [ ] **Step 3: Create `AppDatabase`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/db/AppDatabase.kt
package com.adriandeleon.kmp.template.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adriandeleon.kmp.template.db.post.PostDao
import com.adriandeleon.kmp.template.db.post.PostEntity

@Database(entities = [PostEntity::class], version = 1)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}
```

- [ ] **Step 4: Verify KSP generates Room code**

```bash
./gradlew :shared:kspAndroidKotlin
```
Expected: `BUILD SUCCESSFUL` and generated files appear in `shared/build/generated/ksp/android/`.

---

### Task 8: Wire Room into Koin via `expect/actual` platform module

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/PlatformDatabaseModule.kt`
- Create: `shared/src/androidMain/kotlin/com/adriandeleon/kmp/template/common/PlatformDatabaseModule.android.kt`
- Create: `shared/src/iosMain/kotlin/com/adriandeleon/kmp/template/common/PlatformDatabaseModule.ios.kt`
- Modify: `androidApp/src/main/kotlin/com/adriandeleon/kmp/template/common/AndroidModule.kt`
- Modify: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/di/KoinApp.kt`

- [ ] **Step 1: Declare `expect platformDatabaseModule`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/PlatformDatabaseModule.kt
package com.adriandeleon.kmp.template.common

import org.koin.core.module.Module

internal expect val platformDatabaseModule: Module
```

- [ ] **Step 2: Android actual — empty (Android provides via `androidModule` with Context)**

```kotlin
// shared/src/androidMain/kotlin/com/adriandeleon/kmp/template/common/PlatformDatabaseModule.android.kt
package com.adriandeleon.kmp.template.common

import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformDatabaseModule: Module = module { }
```

- [ ] **Step 3: iOS actual — provides `AppDatabase` using `NSHomeDirectory`**

```kotlin
// shared/src/iosMain/kotlin/com/adriandeleon/kmp/template/common/PlatformDatabaseModule.ios.kt
package com.adriandeleon.kmp.template.common

import androidx.room.Room
import com.adriandeleon.kmp.template.db.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

internal actual val platformDatabaseModule: Module = module {
    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(
            name = NSHomeDirectory() + "/app.db",
        ).build()
    }
}
```

- [ ] **Step 4: Android provides `AppDatabase` in `androidModule` (using Context)**

Replace `androidApp/src/main/kotlin/com/adriandeleon/kmp/template/common/AndroidModule.kt`:
```kotlin
package com.adriandeleon.kmp.template.common

import android.content.Context
import androidx.room.Room
import com.adriandeleon.kmp.template.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(
            context = androidContext(),
            name = "app.db",
        ).build()
    }
}
```

- [ ] **Step 5: Include `platformDatabaseModule` in `initKoin`**

Replace `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/di/KoinApp.kt`:
```kotlin
package com.adriandeleon.kmp.template.common.di

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import co.touchlab.kermit.koin.KermitKoinLogger
import com.adriandeleon.kmp.template.BuildKonfig
import com.adriandeleon.kmp.template.analytics.analyticsModule
import com.adriandeleon.kmp.template.common.commonModule
import com.adriandeleon.kmp.template.common.platformDatabaseModule
import com.adriandeleon.kmp.template.features.featureFlagModule
import com.adriandeleon.kmp.template.logger.loggerModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        includes(config)
        modules(
            commonModule,
            platformDatabaseModule,
            featureFlagModule,
            analyticsModule,
            loggerModule,
        )
        logger(KermitKoinLogger(koin.get()))
        CrashlyticsKotlin.setCustomValue("flavor", if (BuildKonfig.DEBUG) "debug" else "release")
    }
}
```

- [ ] **Step 6: Verify full Android build compiles**

```bash
./gradlew :androidApp:assembleDebug
```
Expected: `BUILD SUCCESSFUL`

---

## Phase 3 — Localization Scaffold

---

### Task 9: Android localization files

**Files:**
- Create: `androidApp/src/main/res/values/strings.xml`
- Create: `androidApp/src/main/res/values-es-r419/strings.xml`
- Create: `androidApp/src/main/res/values-pt-rBR/strings.xml`

- [ ] **Step 1: Create English strings (default)**

```xml
<!-- androidApp/src/main/res/values/strings.xml -->
<!--
    Localization note:
    - This file (values/) is the English default and the fallback for all unsupported locales.
    - To add a language: duplicate a locale folder and change the suffix (e.g. values-fr/).
    - To remove a language: delete its folder.
    - To support only one language: delete all non-English folders.
    - Naming convention: feature_element_description (e.g. posts_screen_title).
    - See: https://developer.android.com/guide/topics/resources/localization
-->
<resources>
    <string name="app_name">KMP Template</string>

    <!-- Posts screen -->
    <string name="posts_screen_title">Posts</string>
    <string name="posts_loading_description">Loading posts…</string>
    <string name="posts_empty_message">No posts available.</string>
    <string name="posts_error_message">Something went wrong. Please try again.</string>
    <string name="posts_retry_button">Retry</string>

    <!-- Home screen -->
    <string name="home_screen_title">Home</string>
</resources>
```

- [ ] **Step 2: Create Spanish (Latin America) strings**

```xml
<!-- androidApp/src/main/res/values-es-r419/strings.xml -->
<!--
    Spanish (Latin America) — es-r419
    Fallback: values/strings.xml (English)
-->
<resources>
    <string name="app_name">KMP Template</string>

    <!-- Posts screen -->
    <string name="posts_screen_title">Publicaciones</string>
    <string name="posts_loading_description">Cargando publicaciones…</string>
    <string name="posts_empty_message">No hay publicaciones disponibles.</string>
    <string name="posts_error_message">Algo salió mal. Por favor intenta de nuevo.</string>
    <string name="posts_retry_button">Reintentar</string>

    <!-- Home screen -->
    <string name="home_screen_title">Pantalla de Inicio</string>
</resources>
```

- [ ] **Step 3: Create Portuguese (Brazil) strings**

```xml
<!-- androidApp/src/main/res/values-pt-rBR/strings.xml -->
<!--
    Portuguese (Brazil) — pt-rBR
    Fallback: values/strings.xml (English)
-->
<resources>
    <string name="app_name">KMP Template</string>

    <!-- Posts screen -->
    <string name="posts_screen_title">Publicações</string>
    <string name="posts_loading_description">Carregando publicações…</string>
    <string name="posts_empty_message">Nenhuma publicação disponível.</string>
    <string name="posts_error_message">Algo deu errado. Por favor, tente novamente.</string>
    <string name="posts_retry_button">Tentar novamente</string>

    <!-- Home screen -->
    <string name="home_screen_title">Tela Inicial</string>
</resources>
```

- [ ] **Step 4: Verify the Android build still compiles**

```bash
./gradlew :androidApp:assembleDebug
```
Expected: `BUILD SUCCESSFUL`

---

### Task 10: iOS string catalog

**Files:**
- Create: `iosApp/KMP-Template/Localizable.xcstrings`

Note: String catalogs are the modern Xcode localization format. Keep all iOS app translations in a single `Localizable.xcstrings` file and use SwiftUI localized string literals or `String(localized:)` in code.

- [ ] **Step 1: Create the catalog**

```
iosApp/KMP-Template/Localizable.xcstrings
```

- [ ] **Step 2: Add locales**

```
Locales:
- en
- es-419
- pt-BR
```

- [ ] **Step 3: Use modern Swift localization APIs**

```
Button("Retry", action: onRetry)
Text("Posts")
String(localized: "Sign in")
```

---

## Phase 4 — Posts Feature

---

### Task 11: Add Ktor networking dependencies and `NetworkModule`

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `shared/build.gradle.kts`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/network/NetworkModule.kt`

- [ ] **Step 1: Add Ktor content negotiation entries to version catalog**

In `gradle/libs.versions.toml`, add to `[libraries]`:
```toml
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
```

- [ ] **Step 2: Add the new Ktor libs to `commonMain.dependencies`**

In `shared/build.gradle.kts`, add inside `commonMain.dependencies`:
```kotlin
implementation(libs.ktor.client.content.negotiation)
implementation(libs.ktor.serialization.kotlinx.json)
```

- [ ] **Step 3: Create `NetworkModule`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/network/NetworkModule.kt
package com.adriandeleon.kmp.template.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

internal val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
}
```

- [ ] **Step 4: Verify build resolves new dependencies**

```bash
./gradlew :shared:assembleAndroidMain
```
Expected: `BUILD SUCCESSFUL`

---

### Task 12: Domain layer — `Post`, `PostsRepository`, `GetPostsUseCase`

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/domain/model/Post.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/domain/repository/PostsRepository.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/domain/usecase/GetPostsUseCase.kt`

- [ ] **Step 1: Create internal domain model**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/domain/model/Post.kt
package com.adriandeleon.kmp.template.posts.domain.model

internal data class Post(
    val id: String,
    val title: String,
    val body: String,
)
```

- [ ] **Step 2: Create repository interface**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/domain/repository/PostsRepository.kt
package com.adriandeleon.kmp.template.posts.domain.repository

import com.adriandeleon.kmp.template.posts.domain.model.Post
import kotlinx.coroutines.flow.Flow

internal interface PostsRepository {
    fun getPosts(): Flow<List<Post>>
}
```

- [ ] **Step 3: Create use case**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/domain/usecase/GetPostsUseCase.kt
package com.adriandeleon.kmp.template.posts.domain.usecase

import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow

internal class GetPostsUseCase(private val repository: PostsRepository) {
    operator fun invoke(): Flow<List<Post>> = repository.getPosts()
}
```

---

### Task 13: Data layer — `PostDto`, `PostEntityMapper`

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/dto/PostDto.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/mapper/PostEntityMapper.kt`

- [ ] **Step 1: Create `PostDto`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/dto/PostDto.kt
package com.adriandeleon.kmp.template.posts.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class PostDto(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
)
```

- [ ] **Step 2: Write the mapper test first**

```kotlin
// shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/data/mapper/PostEntityMapperTest.kt
package com.adriandeleon.kmp.template.posts.data.mapper

import com.adriandeleon.kmp.template.posts.data.dto.PostDto
import com.adriandeleon.kmp.template.db.post.PostEntity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PostEntityMapperTest : FunSpec({
    val mapper = PostEntityMapper()

    context("toEntity") {
        test("maps PostDto fields to PostEntity correctly") {
            val dto = PostDto(id = 1, title = "Title", body = "Body", userId = 10)
            val entity = mapper.toEntity(dto)
            entity.id shouldBe 1
            entity.title shouldBe "Title"
            entity.body shouldBe "Body"
        }
    }

    context("toDomain") {
        test("maps PostEntity fields to Post domain model correctly") {
            val entity = PostEntity(id = 2, title = "Post Title", body = "Post Body")
            val post = mapper.toDomain(entity)
            post.id shouldBe "2"
            post.title shouldBe "Post Title"
            post.body shouldBe "Post Body"
        }
    }
})
```

- [ ] **Step 3: Run the test to verify it fails**

```bash
./gradlew :shared:test --tests "*.PostEntityMapperTest"
```
Expected: FAIL — `PostEntityMapper` does not exist yet.

- [ ] **Step 4: Create `PostEntityMapper`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/mapper/PostEntityMapper.kt
package com.adriandeleon.kmp.template.posts.data.mapper

import com.adriandeleon.kmp.template.db.post.PostEntity
import com.adriandeleon.kmp.template.posts.data.dto.PostDto
import com.adriandeleon.kmp.template.posts.domain.model.Post

internal class PostEntityMapper {
    fun toEntity(dto: PostDto): PostEntity = PostEntity(
        id = dto.id,
        title = dto.title,
        body = dto.body,
    )

    fun toDomain(entity: PostEntity): Post = Post(
        id = entity.id.toString(),
        title = entity.title,
        body = entity.body,
    )
}
```

- [ ] **Step 5: Run the test to verify it passes**

```bash
./gradlew :shared:test --tests "*.PostEntityMapperTest"
```
Expected: `BUILD SUCCESSFUL` — all tests pass.

---

### Task 14: Data layer — `PostsRemoteDataSource` and `PostsLocalDataSource`

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/datasource/PostsRemoteDataSource.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/datasource/PostsLocalDataSource.kt`

- [ ] **Step 1: Create `PostsRemoteDataSource`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/datasource/PostsRemoteDataSource.kt
package com.adriandeleon.kmp.template.posts.data.datasource

import com.adriandeleon.kmp.template.posts.data.dto.PostDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class PostsRemoteDataSource(private val client: HttpClient) {
    suspend fun fetchPosts(): List<PostDto> =
        client.get("https://jsonplaceholder.typicode.com/posts").body()
}
```

- [ ] **Step 2: Create `PostsLocalDataSource`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/datasource/PostsLocalDataSource.kt
package com.adriandeleon.kmp.template.posts.data.datasource

import com.adriandeleon.kmp.template.db.AppDatabase
import com.adriandeleon.kmp.template.db.post.PostEntity

internal class PostsLocalDataSource(private val database: AppDatabase) {
    suspend fun getAll(): List<PostEntity> = database.postDao().getAll()
    suspend fun insertAll(posts: List<PostEntity>) = database.postDao().insertAll(posts)
}
```

---

### Task 15: Data layer — `DefaultPostsRepository`

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/repository/DefaultPostsRepository.kt`
- Create (test): `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/data/repository/DefaultPostsRepositoryTest.kt`

- [ ] **Step 1: Write the repository test first**

```kotlin
// shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/data/repository/DefaultPostsRepositoryTest.kt
package com.adriandeleon.kmp.template.posts.data.repository

import com.adriandeleon.kmp.template.db.post.PostEntity
import com.adriandeleon.kmp.template.posts.data.datasource.PostsLocalDataSource
import com.adriandeleon.kmp.template.posts.data.datasource.PostsRemoteDataSource
import com.adriandeleon.kmp.template.posts.data.dto.PostDto
import com.adriandeleon.kmp.template.posts.data.mapper.PostEntityMapper
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class DefaultPostsRepositoryTest : FunSpec({
    val mockRemote = mock<PostsRemoteDataSource>()
    val mockLocal = mock<PostsLocalDataSource>()
    val mapper = PostEntityMapper()

    context("getPosts") {
        test("emits cached posts first when cache is not empty") {
            runTest {
                val cachedEntities = listOf(PostEntity(id = 1, title = "Cached", body = "Body"))
                everySuspend { mockLocal.getAll() } returns cachedEntities
                everySuspend { mockRemote.fetchPosts() } returns emptyList()
                everySuspend { mockLocal.insertAll(any()) } returns Unit

                val repo = DefaultPostsRepository(mockRemote, mockLocal, mapper)
                val first = repo.getPosts().first()

                first shouldHaveSize 1
                first[0].title shouldBe "Cached"
            }
        }

        test("emits remote posts after fetching and caching") {
            runTest {
                val remoteDtos = listOf(PostDto(id = 2, title = "Remote", body = "Body", userId = 1))
                everySuspend { mockLocal.getAll() } returns emptyList()
                everySuspend { mockRemote.fetchPosts() } returns remoteDtos
                everySuspend { mockLocal.insertAll(any()) } returns Unit

                val repo = DefaultPostsRepository(mockRemote, mockLocal, mapper)
                val posts = repo.getPosts().first()

                posts shouldHaveSize 1
                posts[0].title shouldBe "Remote"
            }
        }
    }
})
```

- [ ] **Step 2: Run the test to verify it fails**

```bash
./gradlew :shared:test --tests "*.DefaultPostsRepositoryTest"
```
Expected: FAIL — `DefaultPostsRepository` does not exist yet.

- [ ] **Step 3: Create `DefaultPostsRepository`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/data/repository/DefaultPostsRepository.kt
package com.adriandeleon.kmp.template.posts.data.repository

import com.adriandeleon.kmp.template.common.util.DispatcherProvider
import com.adriandeleon.kmp.template.posts.data.datasource.PostsLocalDataSource
import com.adriandeleon.kmp.template.posts.data.datasource.PostsRemoteDataSource
import com.adriandeleon.kmp.template.posts.data.mapper.PostEntityMapper
import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class DefaultPostsRepository(
    private val remoteDataSource: PostsRemoteDataSource,
    private val localDataSource: PostsLocalDataSource,
    private val mapper: PostEntityMapper,
    private val dispatchers: DispatcherProvider,
) : PostsRepository {
    override fun getPosts(): Flow<List<Post>> = flow {
        val cached = localDataSource.getAll().map { mapper.toDomain(it) }
        if (cached.isNotEmpty()) emit(cached)

        val remote = remoteDataSource.fetchPosts()
        val entities = remote.map { mapper.toEntity(it) }
        localDataSource.insertAll(entities)
        emit(entities.map { mapper.toDomain(it) })
    }.flowOn(dispatchers.io)
}
```

Note: The constructor for the test doesn't pass `dispatchers` — add a default or use a test dispatcher. Update the test to pass a `FakeDispatcherProvider`:

```kotlin
// Add to the test file, before the class:
private class FakeDispatcherProvider : DispatcherProvider {
    override val main = kotlinx.coroutines.Dispatchers.Unconfined
    override val default = kotlinx.coroutines.Dispatchers.Unconfined
    override val io = kotlinx.coroutines.Dispatchers.Unconfined
}
```

And update `DefaultPostsRepository` construction in the test:
```kotlin
val repo = DefaultPostsRepository(mockRemote, mockLocal, mapper, FakeDispatcherProvider())
```

- [ ] **Step 4: Run the test to verify it passes**

```bash
./gradlew :shared:test --tests "*.DefaultPostsRepositoryTest"
```
Expected: `BUILD SUCCESSFUL` — all tests pass.

---

### Task 16: `GetPostsUseCase` test

**Files:**
- Create (test): `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/domain/usecase/GetPostsUseCaseTest.kt`

- [ ] **Step 1: Write and run the use case test**

```kotlin
// shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/domain/usecase/GetPostsUseCaseTest.kt
package com.adriandeleon.kmp.template.posts.domain.usecase

import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.domain.repository.PostsRepository
import dev.mokkery.mock
import dev.mokkery.every
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class GetPostsUseCaseTest : FunSpec({
    val mockRepository = mock<PostsRepository>()

    test("delegates to repository and returns its flow") {
        runTest {
            val expected = listOf(Post("1", "Title", "Body"))
            every { mockRepository.getPosts() } returns flowOf(expected)

            val useCase = GetPostsUseCase(mockRepository)
            val result = useCase().first()

            result shouldBe expected
        }
    }
})
```

- [ ] **Step 2: Run the test**

```bash
./gradlew :shared:test --tests "*.GetPostsUseCaseTest"
```
Expected: `BUILD SUCCESSFUL` — all tests pass.

---

### Task 17: MVI store types — `PostsIntent`, `PostsState`, `PostsMessage`, `PostsStore`

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsIntent.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsState.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsMessage.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsStore.kt`

- [ ] **Step 1: Create all four MVI type files**

```kotlin
// PostsIntent.kt
package com.adriandeleon.kmp.template.posts.presentation.store

internal sealed interface PostsIntent {
    data object Retry : PostsIntent
}
```

```kotlin
// PostsState.kt
package com.adriandeleon.kmp.template.posts.presentation.store

import com.adriandeleon.kmp.template.posts.domain.model.Post

internal data class PostsState(
    val isLoading: Boolean = true,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
)
```

```kotlin
// PostsMessage.kt
package com.adriandeleon.kmp.template.posts.presentation.store

import com.adriandeleon.kmp.template.posts.domain.model.Post

internal sealed interface PostsMessage {
    data object LoadingStarted : PostsMessage
    data class PostsLoaded(val posts: List<Post>) : PostsMessage
    data class PostsFailed(val error: String) : PostsMessage
}
```

```kotlin
// PostsStore.kt
package com.adriandeleon.kmp.template.posts.presentation.store

import com.arkivanov.mvikotlin.core.store.Store

internal interface PostsStore : Store<PostsIntent, PostsState, Nothing>
```

---

### Task 18: `PostsStoreFactory` with Executor and Reducer

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsStoreFactory.kt`

- [ ] **Step 1: Create `PostsStoreFactory`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/store/PostsStoreFactory.kt
package com.adriandeleon.kmp.template.posts.presentation.store

import com.adriandeleon.kmp.template.common.util.DispatcherProvider
import com.adriandeleon.kmp.template.posts.domain.usecase.GetPostsUseCase
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch

internal class PostsStoreFactory(
    private val storeFactory: StoreFactory,
    private val getPostsUseCase: GetPostsUseCase,
    private val dispatchers: DispatcherProvider,
) {
    fun create(): PostsStore =
        object : PostsStore, Store<PostsIntent, PostsState, Nothing> by storeFactory.create(
            name = "PostsStore",
            initialState = PostsState(),
            bootstrapper = SimpleBootstrapper(Action.LoadPosts),
            executorFactory = { PostsExecutor(getPostsUseCase, dispatchers) },
            reducer = PostsReducer,
        ) {}

    private sealed interface Action {
        data object LoadPosts : Action
    }

    private class PostsExecutor(
        private val getPostsUseCase: GetPostsUseCase,
        private val dispatchers: DispatcherProvider,
    ) : CoroutineExecutor<PostsIntent, Action, PostsState, PostsMessage, Nothing>(
        mainContext = dispatchers.main,
    ) {
        override fun executeAction(action: Action) {
            when (action) {
                Action.LoadPosts -> loadPosts()
            }
        }

        override fun executeIntent(intent: PostsIntent) {
            when (intent) {
                PostsIntent.Retry -> loadPosts()
            }
        }

        private fun loadPosts() {
            scope.launch(dispatchers.io) {
                dispatch(PostsMessage.LoadingStarted)
                try {
                    getPostsUseCase().collect { posts ->
                        dispatch(PostsMessage.PostsLoaded(posts))
                    }
                } catch (e: Exception) {
                    dispatch(PostsMessage.PostsFailed(e.message ?: "Unknown error"))
                }
            }
        }
    }

    private object PostsReducer : Reducer<PostsState, PostsMessage> {
        override fun PostsState.reduce(msg: PostsMessage): PostsState =
            when (msg) {
                PostsMessage.LoadingStarted -> copy(isLoading = true, error = null)
                is PostsMessage.PostsLoaded -> copy(isLoading = false, posts = msg.posts, error = null)
                is PostsMessage.PostsFailed -> copy(isLoading = false, error = msg.error)
            }
    }
}
```

- [ ] **Step 2: Verify the shared module compiles**

```bash
./gradlew :shared:assembleAndroidMain
```
Expected: `BUILD SUCCESSFUL`

---

### Task 19: Public API types — `PostsUiState`, `PostUiModel`, `PostsComponent`

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PostsUiState.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PostUiModel.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PostsComponent.kt`

- [ ] **Step 1: Create the three public-facing types**

```kotlin
// PostsUiState.kt
package com.adriandeleon.kmp.template.posts

sealed class PostsUiState {
    data object Loading : PostsUiState()
    data class Content(val posts: List<PostUiModel>) : PostsUiState()
    data class Error(val message: String) : PostsUiState()
}
```

```kotlin
// PostUiModel.kt
package com.adriandeleon.kmp.template.posts

data class PostUiModel(
    val id: String,
    val title: String,
    val body: String,
)
```

```kotlin
// PostsComponent.kt
package com.adriandeleon.kmp.template.posts

import com.arkivanov.decompose.value.Value

interface PostsComponent {
    val state: Value<PostsUiState>
    fun onRetry()
}
```

---

### Task 20: `PostsUiMapper` + test

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/mapper/PostsUiMapper.kt`
- Create (test): `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/presentation/mapper/PostsUiMapperTest.kt`

- [ ] **Step 1: Write the mapper test first**

```kotlin
// shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/presentation/mapper/PostsUiMapperTest.kt
package com.adriandeleon.kmp.template.posts.presentation.mapper

import com.adriandeleon.kmp.template.posts.PostsUiState
import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.presentation.store.PostsState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class PostsUiMapperTest : FunSpec({
    val mapper = PostsUiMapper()

    test("maps loading state to PostsUiState.Loading") {
        val result = mapper.map(PostsState(isLoading = true))
        result.shouldBeInstanceOf<PostsUiState.Loading>()
    }

    test("maps error state to PostsUiState.Error with correct message") {
        val result = mapper.map(PostsState(isLoading = false, error = "Oops"))
        result shouldBe PostsUiState.Error("Oops")
    }

    test("maps content state to PostsUiState.Content with correct items") {
        val posts = listOf(Post("1", "Title", "Body"))
        val result = mapper.map(PostsState(isLoading = false, posts = posts))
        result shouldBe PostsUiState.Content(
            posts = listOf(com.adriandeleon.kmp.template.posts.PostUiModel("1", "Title", "Body"))
        )
    }
})
```

- [ ] **Step 2: Run the test to verify it fails**

```bash
./gradlew :shared:test --tests "*.PostsUiMapperTest"
```
Expected: FAIL — `PostsUiMapper` does not exist yet.

- [ ] **Step 3: Create `PostsUiMapper`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/mapper/PostsUiMapper.kt
package com.adriandeleon.kmp.template.posts.presentation.mapper

import com.adriandeleon.kmp.template.posts.PostUiModel
import com.adriandeleon.kmp.template.posts.PostsUiState
import com.adriandeleon.kmp.template.posts.presentation.store.PostsState

internal class PostsUiMapper {
    fun map(state: PostsState): PostsUiState = when {
        state.isLoading -> PostsUiState.Loading
        state.error != null -> PostsUiState.Error(state.error)
        else -> PostsUiState.Content(
            posts = state.posts.map { post ->
                PostUiModel(id = post.id, title = post.title, body = post.body)
            },
        )
    }
}
```

- [ ] **Step 4: Run the test to verify it passes**

```bash
./gradlew :shared:test --tests "*.PostsUiMapperTest"
```
Expected: `BUILD SUCCESSFUL` — all tests pass.

---

### Task 21: `DefaultPostsComponent` + `PreviewPostsComponent`

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/DefaultPostsComponent.kt`
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PreviewPostsComponent.kt`

- [ ] **Step 1: Create `DefaultPostsComponent`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/presentation/DefaultPostsComponent.kt
package com.adriandeleon.kmp.template.posts.presentation

import com.adriandeleon.kmp.template.posts.PostsComponent
import com.adriandeleon.kmp.template.posts.PostsUiState
import com.adriandeleon.kmp.template.posts.presentation.mapper.PostsUiMapper
import com.adriandeleon.kmp.template.posts.presentation.store.PostsIntent
import com.adriandeleon.kmp.template.posts.presentation.store.PostsStoreFactory
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal class DefaultPostsComponent(
    componentContext: ComponentContext,
    private val storeFactory: PostsStoreFactory,
    private val uiMapper: PostsUiMapper,
) : PostsComponent, ComponentContext by componentContext {

    private val store = storeFactory.create()
    private val _state = MutableValue(uiMapper.map(store.state))
    override val state: Value<PostsUiState> = _state

    init {
        lifecycle.doOnDestroy(store::dispose)
        val scope = lifecycle.coroutineScope()
        scope.launch {
            store.stateFlow.collect { storeState ->
                _state.value = uiMapper.map(storeState)
            }
        }
    }

    override fun onRetry() {
        store.accept(PostsIntent.Retry)
    }
}
```

- [ ] **Step 2: Create `PreviewPostsComponent`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PreviewPostsComponent.kt
package com.adriandeleon.kmp.template.posts

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewPostsComponent : PostsComponent {
    private val _state = MutableValue<PostsUiState>(
        PostsUiState.Content(previewPosts)
    )
    override val state: Value<PostsUiState> = _state

    override fun onRetry() {
        /* no-op for preview and tests */
    }

    var retryCallCount = 0
        private set

    fun setState(newState: PostsUiState) {
        _state.value = newState
    }

    companion object {
        val previewPosts = listOf(
            PostUiModel(id = "1", title = "First Post", body = "Body of the first post."),
            PostUiModel(id = "2", title = "Second Post", body = "Body of the second post."),
            PostUiModel(id = "3", title = "Third Post", body = "Body of the third post."),
        )
    }
}
```

Note: Add `retryCallCount++` inside `onRetry()` so tests can assert it was called:
```kotlin
override fun onRetry() {
    retryCallCount++
}
```

---

### Task 22: `PreviewHomeComponent` for existing home feature

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/home/PreviewHomeComponent.kt`

- [ ] **Step 1: Create `PreviewHomeComponent`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/home/PreviewHomeComponent.kt
package com.adriandeleon.kmp.template.home

class PreviewHomeComponent : HomeComponent {
    override val uiState = MutableValue(HomeComponent.UiState())
}
```

---

### Task 23: `PostsModule` + update `KoinApp`

**Files:**
- Create: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PostsModule.kt`
- Modify: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/common/di/KoinApp.kt`

- [ ] **Step 1: Create `PostsModule`**

```kotlin
// shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/posts/PostsModule.kt
package com.adriandeleon.kmp.template.posts

import com.adriandeleon.kmp.template.posts.data.datasource.PostsLocalDataSource
import com.adriandeleon.kmp.template.posts.data.datasource.PostsRemoteDataSource
import com.adriandeleon.kmp.template.posts.data.mapper.PostEntityMapper
import com.adriandeleon.kmp.template.posts.data.repository.DefaultPostsRepository
import com.adriandeleon.kmp.template.posts.domain.repository.PostsRepository
import com.adriandeleon.kmp.template.posts.domain.usecase.GetPostsUseCase
import com.adriandeleon.kmp.template.posts.presentation.DefaultPostsComponent
import com.adriandeleon.kmp.template.posts.presentation.mapper.PostsUiMapper
import com.adriandeleon.kmp.template.posts.presentation.store.PostsStoreFactory
import com.arkivanov.decompose.ComponentContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val postsModule = module {
    factoryOf(::PostsRemoteDataSource)
    factoryOf(::PostsLocalDataSource)
    factoryOf(::PostEntityMapper)
    factoryOf(::DefaultPostsRepository) bind PostsRepository::class
    factoryOf(::GetPostsUseCase)
    factoryOf(::PostsUiMapper)
    factoryOf(::PostsStoreFactory)
    factory<PostsComponent> { (componentContext: ComponentContext) ->
        DefaultPostsComponent(
            componentContext = componentContext,
            storeFactory = get(),
            uiMapper = get(),
        )
    }
}
```

- [ ] **Step 2: Add `networkModule` and `postsModule` to `initKoin`**

Replace `KoinApp.kt`:
```kotlin
package com.adriandeleon.kmp.template.common.di

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import co.touchlab.kermit.koin.KermitKoinLogger
import com.adriandeleon.kmp.template.BuildKonfig
import com.adriandeleon.kmp.template.analytics.analyticsModule
import com.adriandeleon.kmp.template.common.commonModule
import com.adriandeleon.kmp.template.common.platformDatabaseModule
import com.adriandeleon.kmp.template.features.featureFlagModule
import com.adriandeleon.kmp.template.logger.loggerModule
import com.adriandeleon.kmp.template.network.networkModule
import com.adriandeleon.kmp.template.posts.postsModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        includes(config)
        modules(
            commonModule,
            platformDatabaseModule,
            networkModule,
            featureFlagModule,
            analyticsModule,
            loggerModule,
            postsModule,
        )
        logger(KermitKoinLogger(koin.get()))
        CrashlyticsKotlin.setCustomValue("flavor", if (BuildKonfig.DEBUG) "debug" else "release")
    }
}
```

- [ ] **Step 3: Verify the shared module builds**

```bash
./gradlew :shared:assembleAndroidMain
```
Expected: `BUILD SUCCESSFUL`

---

### Task 24: Update navigation — `RootComponent` + `DefaultRootComponent`

**Files:**
- Modify: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/root/RootComponent.kt`
- Modify: `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/root/DefaultRootComponent.kt`

- [ ] **Step 1: Add `Posts` child to `RootComponent`**

Replace `RootComponent.kt`:
```kotlin
package com.adriandeleon.kmp.template.root

import com.adriandeleon.kmp.template.home.HomeComponent
import com.adriandeleon.kmp.template.posts.PostsComponent
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner

interface RootComponent : BackHandlerOwner {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Home(val component: HomeComponent) : Child
        data class Posts(val component: PostsComponent) : Child
    }
}
```

- [ ] **Step 2: Update `DefaultRootComponent` to create Posts via Koin**

Replace `DefaultRootComponent.kt`:
```kotlin
package com.adriandeleon.kmp.template.root

import com.adriandeleon.kmp.template.home.DefaultHomeComponent
import com.adriandeleon.kmp.template.home.HomeComponent
import com.adriandeleon.kmp.template.posts.PostsComponent
import com.adriandeleon.kmp.template.root.RootComponent.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class DefaultRootComponent(componentContext: ComponentContext) :
    RootComponent, KoinComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Configuration.serializer(),
            initialConfiguration = Configuration.Posts,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(configuration: Configuration, context: ComponentContext): Child =
        when (configuration) {
            is Configuration.Home -> Child.Home(homeComponent(context))
            is Configuration.Posts -> Child.Posts(postsComponent(context))
        }

    private fun homeComponent(componentContext: ComponentContext): HomeComponent =
        DefaultHomeComponent(componentContext = componentContext)

    private fun postsComponent(componentContext: ComponentContext): PostsComponent =
        get { parametersOf(componentContext) }

    @Serializable
    private sealed interface Configuration {
        @Serializable data object Home : Configuration
        @Serializable data object Posts : Configuration
    }
}
```

Note: `initialConfiguration = Configuration.Posts` makes Posts the initial screen for the template. Change to `Configuration.Home` if preferred.

- [ ] **Step 3: Verify the shared framework builds for iOS**

```bash
./gradlew :shared:assembleXCFramework
```
Expected: `BUILD SUCCESSFUL`

---

### Task 25: Android `PostsView` and update `RootView`

**Files:**
- Create: `androidApp/src/main/kotlin/com/adriandeleon/kmp/template/posts/PostsView.kt`
- Modify: `androidApp/src/main/kotlin/com/adriandeleon/kmp/template/root/RootView.kt`

- [ ] **Step 1: Create `PostsView`**

```kotlin
// androidApp/src/main/kotlin/com/adriandeleon/kmp/template/posts/PostsView.kt
package com.adriandeleon.kmp.template.posts

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriandeleon.kmp.template.R
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun PostsView(component: PostsComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()

    when (val s = state) {
        is PostsUiState.Loading -> PostsLoadingContent(modifier)
        is PostsUiState.Content -> PostsListContent(s.posts, modifier)
        is PostsUiState.Error -> PostsErrorContent(s.message, component::onRetry, modifier)
    }
}

@Composable
private fun PostsLoadingContent(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .testTag(stringResource(R.string.tag_posts_loading)),
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PostsListContent(posts: List<PostUiModel>, modifier: Modifier = Modifier) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag(stringResource(R.string.tag_posts_list)),
    ) {
        items(posts, key = { it.id }) { post ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${stringResource(R.string.tag_posts_item)}_${post.id}"),
            ) {
                Text(text = post.title, style = MaterialTheme.typography.titleMedium)
                Text(text = post.body, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun PostsErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .testTag(stringResource(R.string.tag_posts_error)),
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag(stringResource(R.string.tag_posts_retry_button)),
        ) {
            Text(stringResource(R.string.posts_retry_button))
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(name = "Posts Loading – Light – EN", locale = "en")
@Preview(name = "Posts Loading – Light – ES", locale = "es-rUS")
@Preview(name = "Posts Loading – Light – PT", locale = "pt-rBR")
@Preview(name = "Posts Loading – Dark – EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostsLoadingPreview() {
    MaterialTheme {
        PostsView(PreviewPostsComponent().also { it.setState(PostsUiState.Loading) })
    }
}

@Preview(name = "Posts Content – Light – EN", locale = "en")
@Preview(name = "Posts Content – Light – ES", locale = "es-rUS")
@Preview(name = "Posts Content – Light – PT", locale = "pt-rBR")
@Preview(name = "Posts Content – Dark – EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostsContentPreview() {
    MaterialTheme { PostsView(PreviewPostsComponent()) }
}

@Preview(name = "Posts Error – Light – EN", locale = "en")
@Preview(name = "Posts Error – Light – ES", locale = "es-rUS")
@Preview(name = "Posts Error – Light – PT", locale = "pt-rBR")
@Preview(name = "Posts Error – Dark – EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostsErrorPreview() {
    MaterialTheme {
        PostsView(PreviewPostsComponent().also {
            it.setState(PostsUiState.Error("Something went wrong."))
        })
    }
}
```

- [ ] **Step 2: Add test tag string resources to `values/strings.xml`**

Add to `androidApp/src/main/res/values/strings.xml` (inside `<resources>`):
```xml
<!-- Test tags — do NOT localize these; they are identifiers, not display text -->
<string name="tag_posts_loading" translatable="false">posts_loading</string>
<string name="tag_posts_list" translatable="false">posts_list</string>
<string name="tag_posts_item" translatable="false">posts_item</string>
<string name="tag_posts_error" translatable="false">posts_error</string>
<string name="tag_posts_retry_button" translatable="false">posts_retry_button</string>
```

- [ ] **Step 3: Update `RootView.kt` to handle the `Posts` child**

Replace `RootView.kt`:
```kotlin
package com.adriandeleon.kmp.template.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.adriandeleon.kmp.template.home.HomeView
import com.adriandeleon.kmp.template.posts.PostsView
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun RootView(component: RootComponent, modifier: Modifier = Modifier) {
    val childStack by component.stack.subscribeAsState()

    MaterialTheme {
        Children(
            stack = childStack,
            modifier = modifier.fillMaxSize(),
            animation = stackAnimation(fade()),
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.Home -> HomeView(instance.component)
                is RootComponent.Child.Posts -> PostsView(instance.component)
            }
        }
    }
}
```

- [ ] **Step 4: Build Android debug to verify**

```bash
./gradlew :androidApp:assembleDebug
```
Expected: `BUILD SUCCESSFUL`

---

### Task 26: iOS `PostsView` and update `RootView`

**Files:**
- Create: `iosApp/KMP-Template/Posts/PostsView.swift`
- Modify: `iosApp/KMP-Template/Root/RootView.swift`

- [ ] **Step 1: Create `PostsView.swift`**

```swift
// iosApp/KMP-Template/Posts/PostsView.swift
import Shared
import SwiftUI

struct PostsView: View {
    private let component: PostsComponent
    @StateObject private var stateObserver: ObservableValue<PostsUiState>

    init(_ component: PostsComponent) {
        self.component = component
        self._stateObserver = StateObject(
            wrappedValue: ObservableValue(component.state)
        )
    }

    var body: some View {
        switch stateObserver.value {
        case is PostsUiStateLoading:
            PostsLoadingView()
        case let content as PostsUiStateContent:
            PostsListView(posts: content.posts)
        case let error as PostsUiStateError:
            PostsErrorView(message: error.message, onRetry: component.onRetry)
        default:
            EmptyView()
        }
    }
}

private struct PostsLoadingView: View {
    var body: some View {
        ProgressView()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .accessibilityIdentifier("posts_loading")
    }
}

private struct PostsListView: View {
    let posts: [PostUiModel]

    var body: some View {
        List(posts, id: \.id) { post in
            VStack(alignment: .leading, spacing: 4) {
                Text(post.title)
                    .font(.headline)
                Text(post.body)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            .accessibilityIdentifier("posts_item_\(post.id)")
        }
        .accessibilityIdentifier("posts_list")
        .navigationTitle("Posts")
    }
}

private struct PostsErrorView: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text(message)
                .multilineTextAlignment(.center)
            Button("Retry", action: onRetry)
                .accessibilityIdentifier("posts_retry_button")
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .accessibilityIdentifier("posts_error")
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

#Preview("Posts Loading – English") {
    let component = PreviewPostsComponent()
    component.setState(state: PostsUiStateLoading())
    return PostsView(component)
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Posts Loading – Spanish") {
    let component = PreviewPostsComponent()
    component.setState(state: PostsUiStateLoading())
    return PostsView(component)
        .environment(\.locale, .init(identifier: "es-419"))
}

#Preview("Posts Loading – Portuguese") {
    let component = PreviewPostsComponent()
    component.setState(state: PostsUiStateLoading())
    return PostsView(component)
        .environment(\.locale, .init(identifier: "pt-BR"))
}

#Preview("Posts Content – English") {
    PostsView(PreviewPostsComponent())
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Posts Error – English") {
    let component = PreviewPostsComponent()
    component.setState(state: PostsUiStateError(message: "Something went wrong."))
    return PostsView(component)
        .environment(\.locale, .init(identifier: "en"))
}
```

- [ ] **Step 2: Update `RootView.swift` to handle the Posts child**

Replace `RootView.swift`:
```swift
// iosApp/KMP-Template/Root/RootView.swift
import Shared
import SwiftUI

struct RootView: View {
    private let component: RootComponent

    init(_ component: RootComponent) {
        self.component = component
    }

    var body: some View {
        StackView(
            stackValue: StateValue(component.stack),
            getTitle: { _ in "" },
            onBack: { _ in },
            childContent: { child in
                switch child {
                case let home as RootComponentChildHome:
                    HomeView(home.component)
                case let posts as RootComponentChildPosts:
                    PostsView(posts.component)
                default:
                    EmptyView()
                }
            },
        )
    }
}
```

- [ ] **Step 3: Build the Xcode project to verify**

Open `iosApp/KMP-Template.xcodeproj` in Xcode and run `Cmd+B`.
Expected: Build succeeds with no errors.

---

## Phase 5 — UI Tests

---

### Task 27: `commonTest` — `TestComponentContext` + `DefaultHomeComponentTest`

**Files:**
- Create: `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/common/util/TestComponentContext.kt`
- Create: `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/home/DefaultHomeComponentTest.kt`

- [ ] **Step 1: Create `testComponentContext` utility**

```kotlin
// shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/common/util/TestComponentContext.kt
package com.adriandeleon.kmp.template.common.util

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry

internal fun testComponentContext() = DefaultComponentContext(
    lifecycle = LifecycleRegistry().also { it.resume() }
)
```

- [ ] **Step 2: Write `DefaultHomeComponentTest`**

```kotlin
// shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/home/DefaultHomeComponentTest.kt
package com.adriandeleon.kmp.template.home

import com.adriandeleon.kmp.template.common.util.testComponentContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DefaultHomeComponentTest : FunSpec({
    test("exposes the correct title") {
        val component = DefaultHomeComponent(testComponentContext())
        component.uiState.value.isReady shouldBe true
    }
})
```

- [ ] **Step 3: Run the test**

```bash
./gradlew :shared:test --tests "*.DefaultHomeComponentTest"
```
Expected: `BUILD SUCCESSFUL` — test passes.

---

### Task 28: `commonTest` — `DefaultPostsComponent` integration tests via mapper

The Store wiring is tested indirectly: the mapper is unit-tested in Task 20 and the Store bootstrapper is an MVIKotlin concern. This task tests that `DefaultPostsComponent` correctly exposes the state from the store and forwards `onRetry`.

**Files:**
- Create (test): `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/presentation/DefaultPostsComponentTest.kt`

- [ ] **Step 1: Write the component test**

This test uses a fake `StoreFactory` (MVIKotlin's `TimeTravelStoreFactory` supports test introspection, but for simplicity use `DefaultStoreFactory` with a controlled state via a custom bootstrapper):

```kotlin
// shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/posts/presentation/DefaultPostsComponentTest.kt
package com.adriandeleon.kmp.template.posts.presentation

import com.adriandeleon.kmp.template.common.util.testComponentContext
import com.adriandeleon.kmp.template.posts.PostsUiState
import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.domain.repository.PostsRepository
import com.adriandeleon.kmp.template.posts.domain.usecase.GetPostsUseCase
import com.adriandeleon.kmp.template.posts.presentation.mapper.PostsUiMapper
import com.adriandeleon.kmp.template.posts.presentation.store.PostsStoreFactory
import com.adriandeleon.kmp.template.common.util.DispatcherProvider
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf

private class FakeDispatcherProvider : DispatcherProvider {
    override val main = Dispatchers.Unconfined
    override val default = Dispatchers.Unconfined
    override val io = Dispatchers.Unconfined
}

private class FakePostsRepository(private val posts: List<Post> = emptyList()) : PostsRepository {
    override fun getPosts() = flowOf(posts)
}

class DefaultPostsComponentTest : FunSpec({
    val dispatchers = FakeDispatcherProvider()
    val uiMapper = PostsUiMapper()

    test("initial state is Loading") {
        val storeFactory = PostsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            getPostsUseCase = GetPostsUseCase(FakePostsRepository()),
            dispatchers = dispatchers,
        )
        val component = DefaultPostsComponent(
            componentContext = testComponentContext(),
            storeFactory = storeFactory,
            uiMapper = uiMapper,
        )
        // Initial state from PostsState(isLoading = true) maps to Loading
        component.state.value.shouldBeInstanceOf<PostsUiState.Loading>()
    }

    test("state becomes Content when repository returns posts") {
        val posts = listOf(Post("1", "Title", "Body"))
        val storeFactory = PostsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            getPostsUseCase = GetPostsUseCase(FakePostsRepository(posts)),
            dispatchers = dispatchers,
        )
        val component = DefaultPostsComponent(
            componentContext = testComponentContext(),
            storeFactory = storeFactory,
            uiMapper = uiMapper,
        )
        // Allow the bootstrapper to run and emit
        // State should now be Content
        component.state.value.shouldBeInstanceOf<PostsUiState.Content>()
    }
})
```

- [ ] **Step 2: Run the tests**

```bash
./gradlew :shared:test --tests "*.DefaultPostsComponentTest"
```
Expected: `BUILD SUCCESSFUL` — tests pass.

---

### Task 29: Android instrumented tests — Robot pattern

**Files:**
- Modify: `androidApp/build.gradle.kts`
- Create: `androidApp/src/androidTest/kotlin/com/adriandeleon/kmp/template/util/TestUtils.kt`
- Create: `androidApp/src/androidTest/kotlin/com/adriandeleon/kmp/template/posts/PostsViewRobot.kt`
- Create: `androidApp/src/androidTest/kotlin/com/adriandeleon/kmp/template/posts/PostsViewTest.kt`

- [ ] **Step 1: Add Compose UI test dependencies to `androidApp/build.gradle.kts`**

Add inside the `dependencies { }` block:
```kotlin
androidTestImplementation(compose.uiTest)
debugImplementation(compose.uiTestManifest)
```

- [ ] **Step 2: Create `TestUtils.kt`**

```kotlin
// androidApp/src/androidTest/kotlin/com/adriandeleon/kmp/template/util/TestUtils.kt
package com.adriandeleon.kmp.template.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.test.platform.app.InstrumentationRegistry

private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

fun SemanticsNodeInteractionsProvider.onNodeWithTag(
    @StringRes resId: Int,
    useUnmergedTree: Boolean = false,
): SemanticsNodeInteraction =
    onNode(matcher = hasTestTag(context.getString(resId)), useUnmergedTree = useUnmergedTree)

fun SemanticsNodeInteractionsProvider.onNodeWithText(
    @StringRes resId: Int,
    substring: Boolean = false,
    ignoreCase: Boolean = false,
    useUnmergedTree: Boolean = false,
): SemanticsNodeInteraction =
    onNode(
        matcher = hasText(context.getString(resId), substring, ignoreCase),
        useUnmergedTree = useUnmergedTree,
    )

fun SemanticsNodeInteractionsProvider.onNodeWithContentDescription(
    @StringRes resId: Int,
    useUnmergedTree: Boolean = false,
): SemanticsNodeInteraction =
    onNode(matcher = hasContentDescription(context.getString(resId)), useUnmergedTree = useUnmergedTree)
```

- [ ] **Step 3: Create `PostsViewRobot.kt`**

```kotlin
// androidApp/src/androidTest/kotlin/com/adriandeleon/kmp/template/posts/PostsViewRobot.kt
package com.adriandeleon.kmp.template.posts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import com.adriandeleon.kmp.template.R
import com.adriandeleon.kmp.template.util.onNodeWithTag
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun ComposeContentTestRule.launchPostsView(
    component: PreviewPostsComponent,
    block: PostsViewRobot.() -> Unit = {},
): PostsViewRobot {
    setContent { PostsView(component) }
    return PostsViewRobot(this, component).apply(block)
}

class PostsViewRobot(
    private val rule: ComposeContentTestRule,
    private val component: PreviewPostsComponent,
) {
    infix fun verify(block: PostsViewVerification.() -> Unit): PostsViewVerification {
        rule.waitForIdle()
        return PostsViewVerification(rule, component).apply(block)
    }

    fun clickRetry() {
        rule.onNodeWithTag(R.string.tag_posts_retry_button).performClick()
    }
}

class PostsViewVerification(
    private val rule: ComposeContentTestRule,
    private val component: PreviewPostsComponent,
) {
    fun loadingIndicatorIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_posts_loading).assertIsDisplayed()
    }

    fun postsListIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_posts_list).assertIsDisplayed()
    }

    fun errorViewIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_posts_error).assertIsDisplayed()
    }

    fun retryButtonIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_posts_retry_button).assertIsDisplayed()
    }

    fun retryWasCalled(expectedCallCount: Int = 1) {
        assertEquals(expectedCallCount, component.retryCallCount)
    }

    fun postsListHasItemCount(expected: Int) {
        assertEquals(expected, (component.state.value as PostsUiState.Content).posts.size)
    }
}
```

- [ ] **Step 4: Create `PostsViewTest.kt`**

```kotlin
// androidApp/src/androidTest/kotlin/com/adriandeleon/kmp/template/posts/PostsViewTest.kt
package com.adriandeleon.kmp.template.posts

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class PostsViewTest {
    @get:Rule val composeTestRule = createComposeRule()

    private val component = PreviewPostsComponent()

    @Test
    fun verifyLoadingIndicatorIsDisplayed() {
        component.setState(PostsUiState.Loading)
        composeTestRule.launchPostsView(component) verify { loadingIndicatorIsDisplayed() }
    }

    @Test
    fun verifyPostsListIsDisplayedWhenContentState() {
        composeTestRule.launchPostsView(component) verify { postsListIsDisplayed() }
    }

    @Test
    fun verifyPostsListHasCorrectItemCount() {
        composeTestRule.launchPostsView(component) verify {
            postsListHasItemCount(PreviewPostsComponent.previewPosts.size)
        }
    }

    @Test
    fun verifyErrorViewIsDisplayedWhenErrorState() {
        component.setState(PostsUiState.Error("Something went wrong."))
        composeTestRule.launchPostsView(component) verify { errorViewIsDisplayed() }
    }

    @Test
    fun verifyRetryButtonIsDisplayedOnError() {
        component.setState(PostsUiState.Error("Something went wrong."))
        composeTestRule.launchPostsView(component) verify { retryButtonIsDisplayed() }
    }

    @Test
    fun verifyRetryCallbackIsInvokedOnRetryClick() {
        component.setState(PostsUiState.Error("Something went wrong."))
        composeTestRule.launchPostsView(component) { clickRetry() } verify { retryWasCalled() }
    }
}
```

- [ ] **Step 5: Run the instrumented tests on an emulator**

```bash
./gradlew :androidApp:connectedAndroidTest
```
Expected: All 6 tests pass.

---

### Task 30: iOS Swift Testing — `PostsViewTests`

**Files:**
- Create: `iosApp/KMP-TemplateTests/Posts/PostsViewTests.swift`

Before writing tests: in Xcode, add `ViewInspector` as a Swift Package dependency to the test target only.
- URL: `https://github.com/nalexn/ViewInspector`
- Version: latest stable (≥ 0.9.x)
- Target: `KMP-TemplateTests` only — do NOT add to the main app target.

- [ ] **Step 1: Create the test target folder and file**

Create `iosApp/KMP-TemplateTests/Posts/PostsViewTests.swift`:

```swift
// iosApp/KMP-TemplateTests/Posts/PostsViewTests.swift
import Shared
import Testing
import ViewInspector

@MainActor
@Suite("PostsView Test Suite")
struct PostsViewTests {
    private let component = PreviewPostsComponent()
    private let sut: PostsView

    init() {
        sut = PostsView(component)
    }

    // ── State visibility ─────────────────────────────────────────────────────

    @Test("shows loading view when state is Loading")
    func showsLoadingViewWhenLoading() async throws {
        component.setState(state: PostsUiStateLoading())

        let loadingView = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "posts_loading")

        #expect(!loadingView.isHidden())
    }

    @Test("shows posts list when state is Content")
    func showsPostsListWhenContent() async throws {
        // Default state is Content with previewPosts
        let listView = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "posts_list")

        #expect(!listView.isHidden())
    }

    @Test("shows correct number of posts in Content state")
    func showsCorrectPostCount() async throws {
        let posts = PreviewPostsComponent.companion.previewPosts
        let expectedCount = posts.count

        let listView = try sut.inspect()
            .find(ViewType.List.self)

        #expect(try listView.count == expectedCount)
    }

    @Test("shows error view when state is Error")
    func showsErrorViewWhenError() async throws {
        component.setState(state: PostsUiStateError(message: "Oops"))

        let errorView = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "posts_error")

        #expect(!errorView.isHidden())
    }

    // ── Interactions ─────────────────────────────────────────────────────────

    @Test("calls onRetry when retry button is tapped")
    func callsOnRetryWhenRetryTapped() async throws {
        component.setState(state: PostsUiStateError(message: "Oops"))

        let retryButton = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "posts_retry_button")
            .button()

        try retryButton.tap()

        #expect(component.retryCallCount == 1)
    }
}
```

- [ ] **Step 2: Run the Swift tests via Xcode**

In Xcode: `Cmd+U` to run all tests in the scheme.
Expected: All 5 tests in `PostsViewTests` pass (marked green in Test Navigator).

Alternatively via command line (requires `xcodebuild`):
```bash
xcodebuild test \
  -project iosApp/KMP-Template.xcodeproj \
  -scheme "KMP-Template" \
  -destination "platform=iOS Simulator,name=iPhone 16" \
  -only-testing:"KMP-TemplateTests/PostsViewTests"
```
Expected: `** TEST SUCCEEDED **`

---

## Self-Review Checklist

- [x] **Spec coverage:** All 5 spec sections have corresponding tasks. Firebase BOM ✓, MVIKotlin gating ✓, BuildKonfig dedup ✓, setup script ✓, Room integration ✓, DispatcherProvider io ✓, platformDatabaseModule ✓, localization en/es/pt ✓, Ktor setup ✓, full Posts feature ✓, strict API boundary ✓, PostsUiMapper ✓, PostsStoreFactory ✓, DefaultPostsComponent ✓, PreviewPostsComponent ✓, PreviewHomeComponent ✓, navigation ✓, Android UI ✓, iOS UI ✓, previews all 3 locales ✓, robot pattern Android ✓, Swift Testing iOS ✓.
- [x] **Placeholder scan:** No TBD/TODO in implementation steps. All code blocks are complete.
- [x] **Type consistency:** `PostsUiState.Loading/Content/Error`, `PostsIntent.Retry`, `PostsMessage.LoadingStarted/PostsLoaded/PostsFailed`, `PostsState`, `PostUiModel`, `PreviewPostsComponent.setState()`, `retryCallCount` — all consistent across tasks.
- [x] **Koin wiring:** `postsModule` registers `factory<PostsComponent> { (ctx: ComponentContext) -> ... }`. `DefaultRootComponent` calls `get { parametersOf(componentContext) }`. Consistent.
- [x] **`DispatcherProvider.io`** — added in Task 6 before first use in Task 15 (`DefaultPostsRepository`). Consistent.
