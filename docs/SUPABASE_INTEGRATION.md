# Supabase Integration Guide

This document covers the complete Supabase integration setup for the Kotlin Multiplatform project, including local development, database management, authentication, and production deployment.

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [🔥 What is Supabase?](#-what-is-supabase)
- [🏗️ Architecture](#️-architecture)
- [⚙️ Setup](#️-setup)
- [🔧 Configuration](#-configuration)
- [💻 Local Development](#-local-development)
- [🗄️ Database Management](#️-database-management)
- [🔐 Authentication](#-authentication)
- [📁 Storage](#-storage)
- [🚀 Production Deployment](#-production-deployment)
- [🐛 Troubleshooting](#-troubleshooting)
- [📚 Best Practices](#-best-practices)

## 🎯 Overview

Supabase provides a comprehensive backend-as-a-service solution with PostgreSQL database, real-time subscriptions, authentication, and storage. This integration enables cross-platform data management, user authentication, and real-time features for both Android and iOS apps.

## 🔥 What is Supabase?

Supabase is an open-source alternative to Firebase that provides:

- **PostgreSQL Database**: Full-featured relational database
- **Real-time Subscriptions**: Live data updates across clients
- **Authentication**: User management with multiple providers
- **Storage**: File upload and management
- **Edge Functions**: Serverless functions for custom logic
- **Auto-generated APIs**: REST and GraphQL APIs from your database

## 🏗️ Architecture

### **Multiplatform Integration**

The Supabase integration follows a layered architecture pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Android UI    │  │    iOS UI       │  │  Shared UI  │ │
│  │  (Compose)      │  │   (SwiftUI)     │  │  (KMP)      │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Business Logic Layer                     │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              Shared Module (Kotlin)                     │ │
│  │  ┌─────────────────┐  ┌─────────────────┐              │ │
│  │  │   Database      │  │  Authentication │              │ │
│  │  │   Operations    │  │   & Storage     │              │ │
│  │  └─────────────────┘  └─────────────────┘              │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Platform Layer                          │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Android       │  │      iOS        │                  │
│  │  Ktor Engine    │  │   Ktor Engine   │                  │ │
│  └─────────────────┘  └─────────────────┘                  │ │
└─────────────────────────────────────────────────────────────┘
```

### **Key Components**

1. **Shared Module**: Platform-agnostic database operations and business logic
2. **Platform Implementations**: Android and iOS specific Ktor client engines
3. **Dependency Injection**: Koin-based service management
4. **BuildKonfig**: Environment-specific configuration management
5. **Database Abstraction**: Repository pattern for data access

## 🚀 Setup

### **1. Dependencies**

#### **Version Catalog Configuration**
```toml
# In gradle/libs.versions.toml
[versions]
supabase = "3.0.3"
ktor = "3.0.3"
buildkonfig = "0.15.2"

[libraries]
# Supabase modules
supabase-auth = { group = "io.github.jan-tennert.supabase", name = "auth-kt", version.ref = "supabase" }
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt", version.ref = "supabase" }
supabase-functions = { group = "io.github.jan-tennert.supabase", name = "functions-kt", version.ref = "supabase" }
supabase-storage = { group = "io.github.jan-tennert.supabase", name = "storage-kt", version.ref = "supabase" }
supabase-realtime = { group = "io.github.jan-tennert.supabase", name = "realtime-kt", version.ref = "supabase" }

# Ktor client engines
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-client-cio = { module = "io.ktor:ktor-client-cio", version.ref = "ktor" }
ktor-client-mock = { module = "io.ktor:ktor-client-mock", version.ref = "ktor" }

[plugins]
buildkonfig = { id = "com.codingfeline.buildkonfig", version.ref = "buildkonfig" }
```

#### **Shared Module Dependencies**
```kotlin
// In shared/build.gradle.kts
plugins {
    alias(libs.plugins.buildkonfig)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Supabase modules
            implementation(libs.supabase.auth)
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.functions)
            implementation(libs.supabase.storage)
            implementation(libs.supabase.realtime)
        }
        
        androidMain.dependencies {
            // Android Ktor engine
            implementation(libs.ktor.client.okhttp)
        }
        
        iosMain.dependencies {
            // iOS Ktor engine
            implementation(libs.ktor.client.darwin)
        }
        
        jvmMain.dependencies {
            // JVM Ktor engine
            implementation(libs.ktor.client.cio)
        }
        
        commonTest.dependencies {
            // Test Ktor engine
            implementation(libs.ktor.client.mock)
        }
    }
}
```

#### **Root Project Configuration**
```kotlin
// In root build.gradle.kts
plugins {
    alias(libs.plugins.buildkonfig) apply false
}
```

### **2. BuildKonfig Plugin**

#### **Configuration Function**
```kotlin
// In shared/build.gradle.kts
fun getSecret(key: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(key) 
        ?: System.getenv(key) 
        ?: throw Exception("Missing secret $key in local.properties or environment variables")
}

buildkonfig {
    packageName = "com.yourcompany.yourapp"
    
    defaultConfigs {
        buildConfigField(BuildKonfigField.STRING, "SUPABASE_URL", getSecret("SUPABASE_URL"))
        buildConfigField(BuildKonfigField.STRING, "SUPABASE_KEY", getSecret("SUPABASE_KEY"))
    }
    
    targetConfigs {
        create("debug") {
            buildConfigField(BuildKonfigField.STRING, "SUPABASE_URL", getSecret("SUPABASE_URL_DEV"))
            buildConfigField(BuildKonfigField.STRING, "SUPABASE_KEY", getSecret("SUPABASE_KEY_DEV"))
        }
        
        create("release") {
            buildConfigField(BuildKonfigField.STRING, "SUPABASE_URL", getSecret("SUPABASE_URL_PROD"))
            buildConfigField(BuildKonfigField.STRING, "SUPABASE_KEY", getSecret("SUPABASE_KEY_PROD"))
        }
    }
}
```

## ⚙️ Configuration

### **1. Environment Configuration**

#### **Development Environment**
```properties
# In local.properties (DO NOT COMMIT)
# Supabase Development Credentials
SUPABASE_URL_DEV_AND=http://10.0.2.2:54321
SUPABASE_URL_DEV_IOS=http://127.0.0.1:54321
SUPABASE_KEY_DEV=your_local_supabase_anon_key # pragma: allowlist secret

# Supabase Production Credentials (for testing)
SUPABASE_URL_PROD=https://your-project.supabase.co
SUPABASE_KEY_PROD=your_production_supabase_anon_key # pragma: allowlist secret
```

#### **Production Environment**
```yaml
# In GitHub Actions workflows
env:
  SUPABASE_URL_DEV_AND: ${{ secrets.SUPABASE_URL_PROD }}
  SUPABASE_URL_DEV_IOS: ${{ secrets.SUPABASE_URL_PROD }}
  SUPABASE_KEY_DEV: ${{ secrets.SUPABASE_KEY_PROD }}
  SUPABASE_URL_PROD: ${{ secrets.SUPABASE_URL_PROD }}
  SUPABASE_KEY_PROD: ${{ secrets.SUPABASE_KEY_PROD }}
```

#### **Build Configuration**
```properties
# In gradle.properties
# For development builds
buildkonfig.flavor=debug

# For production builds
buildkonfig.flavor=release
```

### **2. Supabase Client Setup**

#### **Client Configuration**
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/data/supabase/SupabaseClient.kt
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.functions.Functions
import kotlinx.serialization.json.Json

fun createSupabaseClient(): SupabaseClient = createSupabaseClient(
    supabaseUrl = BuildKonfig.SUPABASE_URL,
    supabaseKey = BuildKonfig.SUPABASE_KEY
) {
    install(GoTrue) {
        // Authentication configuration
        autoLoadFromStorage = true
        autoSaveToStorage = true
    }
    
    install(Postgrest) {
        // Database configuration
        defaultSerializer = KotlinXSerializer()
    }
    
    install(Storage) {
        // Storage configuration
        defaultSerializer = KotlinXSerializer()
    }
    
    install(Realtime) {
        // Real-time configuration
        defaultSerializer = KotlinXSerializer()
    }
    
    install(Functions) {
        // Edge functions configuration
        defaultSerializer = KotlinXSerializer()
    }
}
```

#### **Dependency Injection**
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/di/SupabaseModule.kt
import org.koin.dsl.module

val supabaseModule = module {
    single { createSupabaseClient() }
    single { get<SupabaseClient>().goTrue }
    single { get<SupabaseClient>().postgrest }
    single { get<SupabaseClient>().storage }
    single { get<SupabaseClient>().realtime }
    single { get<SupabaseClient>().functions }
}
```

## 🏠 Local Development

### **1. Prerequisites**

#### **Install Required Tools**
```bash
# Install Supabase CLI
npm install -g supabase

# Install Docker Desktop
# Download from https://docs.docker.com/desktop/
```

#### **Verify Installation**
```bash
# Check Supabase CLI version
supabase --version

# Check Docker installation
docker --version
```

### **2. Project Initialization**

#### **Initialize Supabase Project**
```bash
# In your project root directory
supabase init

# This creates:
# - supabase/ directory
# - config.toml configuration file
# - migrations/ directory for database changes
```

#### **Configure Local Environment**
```toml
# In supabase/config.toml
[api]
enabled = true
port = 54321
schemas = ["public", "storage", "graphql_public"]
extra_search_path = ["public", "extensions"]
max_rows = 1000

[db]
port = 54322
shadow_port = 54320
major_version = 15

[studio]
enabled = true
port = 54323
api_url = "http://127.0.0.1:54321"

[inbucket]
enabled = true
port = 54324
smtp_port = 54325
pop3_port = 54326

[storage]
enabled = true
file_size_limit = "50MiB"

[auth]
enabled = true
port = 54327
site_url = "http://127.0.0.1:3000"
additional_redirect_urls = ["https://localhost:3000"]
jwt_expiry = 3600
enable_signup = true
```

### **3. Local Development Stack**

#### **Start Local Stack**
```bash
# Start all Supabase services
supabase start

# Output will include:
# API URL: http://127.0.0.1:54321
# DB URL: postgresql://postgres:postgres@127.0.0.1:54322/postgres # pragma: allowlist secret
# Studio URL: http://127.0.0.1:54323
# Inbucket URL: http://127.0.0.1:54324
# JWT secret: super-secret-jwt-token-with-at-least-32-characters-long # pragma: allowlist secret
# anon key: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... # pragma: allowlist secret
# service_role key: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... # pragma: allowlist secret
```

#### **Stop Local Stack**
```bash
# Stop services without resetting database
supabase stop

# Stop and reset database
supabase stop --reset
```

### **4. Platform-Specific Configuration**

#### **Android Emulator Configuration**
```xml
<!-- In composeApp/src/androidMain/res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
    </domain-config>
</network-security-config>
```

```xml
<!-- In composeApp/src/androidMain/AndroidManifest.xml -->
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
    <!-- Your app configuration -->
</application>
```

#### **iOS Simulator Configuration**
```swift
// iOS simulator uses localhost (127.0.0.1) directly
// No additional configuration needed
```

## 🗄️ Database Management

### **1. Database Operations**

#### **Create Tables**
```sql
-- In Supabase Studio SQL Editor
CREATE TABLE users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    full_name TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE posts (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    content TEXT,
    published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

#### **Row Level Security (RLS)**
```sql
-- Enable RLS on tables
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE posts ENABLE ROW LEVEL SECURITY;

-- Create policies
CREATE POLICY "Users can view their own profile" ON users
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile" ON users
    FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Anyone can view published posts" ON posts
    FOR SELECT USING (published = true);

CREATE POLICY "Users can view their own posts" ON posts
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can create their own posts" ON posts
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own posts" ON posts
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own posts" ON posts
    FOR DELETE USING (auth.uid() = user_id);
```

### **2. Migrations**

#### **Create Migration**
```bash
# Generate migration from schema changes
supabase db diff --use-migra initial_schema -f initial_schema

# This creates: supabase/migrations/YYYYMMDDHHMMSS_initial_schema.sql
```

#### **Apply Migrations**
```bash
# Apply all pending migrations
supabase migration up

# Reset database and apply all migrations
supabase db reset

# Verify migration status
supabase migration list
```

#### **Seed Data**
```sql
-- In supabase/seed.sql
INSERT INTO users (email, full_name) VALUES
    ('john@example.com', 'John Doe'),
    ('jane@example.com', 'Jane Smith');

INSERT INTO posts (user_id, title, content, published) VALUES
    ((SELECT id FROM users WHERE email = 'john@example.com'), 'First Post', 'Hello World!', true),
    ((SELECT id FROM users WHERE email = 'jane@example.com'), 'Second Post', 'Another post', false);
```

### **3. Database Operations in Kotlin**

#### **Repository Pattern**
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/data/repository/UserRepository.kt
class UserRepository(
    private val postgrest: PostgrestClient
) {
    suspend fun getUsers(): List<User> {
        return postgrest["users"]
            .select()
            .decodeList<User>()
    }
    
    suspend fun getUserById(id: String): User? {
        return postgrest["users"]
            .select { eq("id", id) }
            .decodeSingleOrNull<User>()
    }
    
    suspend fun createUser(user: CreateUserRequest): User {
        return postgrest["users"]
            .insert(user)
            .decodeSingle<User>()
    }
    
    suspend fun updateUser(id: String, updates: UpdateUserRequest): User {
        return postgrest["users"]
            .update(updates) { eq("id", id) }
            .decodeSingle<User>()
    }
    
    suspend fun deleteUser(id: String) {
        postgrest["users"]
            .delete { eq("id", id) }
    }
}
```

#### **Data Models**
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/data/model/User.kt
@Serializable
data class User(
    val id: String,
    val email: String,
    val fullName: String?,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateUserRequest(
    val email: String,
    val fullName: String?
)

@Serializable
data class UpdateUserRequest(
    val fullName: String?
)
```

## 🔐 Authentication

### **1. Authentication Setup**

#### **Enable Auth Providers**
```sql
-- In Supabase Studio > Authentication > Providers
-- Enable Email provider
-- Configure OAuth providers (Google, GitHub, etc.)
```

#### **Authentication Operations**
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/auth/AuthService.kt
class AuthService(
    private val goTrue: GoTrueClient
) {
    suspend fun signUp(email: String, password: String): AuthResponse {
        return goTrue.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }
    
    suspend fun signIn(email: String, password: String): AuthResponse {
        return goTrue.loginWith(Email) {
            this.email = email
            this.password = password
        }
    }
    
    suspend fun signOut() {
        goTrue.logout()
    }
    
    suspend fun resetPassword(email: String) {
        goTrue.resetPasswordForEmail(email)
    }
    
    fun getCurrentUser(): User? {
        return goTrue.currentUserOrNull()
    }
    
    fun getCurrentSession(): Session? {
        return goTrue.currentSessionOrNull()
    }
}
```

#### **Session Management**
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/auth/SessionManager.kt
class SessionManager(
    private val goTrue: GoTrueClient
) {
    fun observeAuthState(): Flow<AuthState> = flow {
        emit(goTrue.currentSessionOrNull()?.let { AuthState.Authenticated(it) } ?: AuthState.Unauthenticated)
        
        goTrue.onAuthStateChange { _, session ->
            emit(session?.let { AuthState.Authenticated(it) } ?: AuthState.Unauthenticated)
        }
    }
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val session: Session) : AuthState()
}
```

### **2. Protected Routes**

#### **Authentication Guard**
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/navigation/AuthGuard.kt
class AuthGuard(
    private val sessionManager: SessionManager
) {
    fun isAuthenticated(): Boolean {
        return sessionManager.getCurrentSession() != null
    }
    
    suspend fun requireAuth(): Session {
        return sessionManager.getCurrentSession() 
            ?: throw UnauthorizedException("Authentication required")
    }
}
```

## 📁 Storage

### **1. Storage Operations**

#### **File Upload**
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/storage/StorageService.kt
class StorageService(
    private val storage: StorageClient
) {
    suspend fun uploadFile(
        bucket: String,
        path: String,
        data: ByteArray,
        contentType: String
    ): FileObject {
        return storage[bucket].upload(
            path = path,
            data = data,
            upsert = true
        )
    }
    
    suspend fun downloadFile(bucket: String, path: String): ByteArray {
        return storage[bucket].download(path)
    }
    
    suspend fun deleteFile(bucket: String, path: String) {
        storage[bucket].delete(path)
    }
    
    suspend fun listFiles(bucket: String, path: String): List<FileObject> {
        return storage[bucket].list(path)
    }
    
    fun getPublicUrl(bucket: String, path: String): String {
        return storage[bucket].publicUrl(path)
    }
}
```

#### **Storage Bucket Configuration**
```sql
-- Create storage bucket
INSERT INTO storage.buckets (id, name, public) VALUES 
    ('avatars', 'avatars', true),
    ('posts', 'posts', false);

-- Set up storage policies
CREATE POLICY "Avatar images are publicly accessible" ON storage.objects
    FOR SELECT USING (bucket_id = 'avatars');

CREATE POLICY "Users can upload avatars" ON storage.objects
    FOR INSERT WITH CHECK (bucket_id = 'avatars' AND auth.uid()::text = (storage.foldername(name))[1]);

CREATE POLICY "Users can update their own avatars" ON storage.objects
    FOR UPDATE USING (bucket_id = 'avatars' AND auth.uid()::text = (storage.foldername(name))[1]);

CREATE POLICY "Users can delete their own avatars" ON storage.objects
    FOR DELETE USING (bucket_id = 'avatars' AND auth.uid()::text = (storage.foldername(name))[1]);
```

## 🚀 Production Deployment

### **1. Remote Project Setup**

#### **Link Local to Remote**
```bash
# Login to Supabase
supabase login

# Link to remote project
supabase link --project-ref your-project-id

# Get project ID from dashboard URL:
# https://supabase.com/dashboard/project/your-project-id
```

#### **Deploy Changes**
```bash
# Push local migrations to remote
supabase db push

# Pull remote changes to local
supabase db pull

# Reset local database from remote
supabase db reset
```

### **2. Environment Variables**

#### **GitHub Secrets**
```yaml
# Required secrets for CI/CD
SUPABASE_URL_PROD: "https://your-project.supabase.co"
SUPABASE_KEY_PROD: "your-production-anon-key"
```

#### **Workflow Configuration**
```yaml
# In .github/workflows/android_deploy.yml and ios_deploy.yml
env:
  SUPABASE_URL_DEV_AND: ${{ secrets.SUPABASE_URL_PROD }}
  SUPABASE_URL_DEV_IOS: ${{ secrets.SUPABASE_URL_PROD }}
  SUPABASE_KEY_DEV: ${{ secrets.SUPABASE_KEY_PROD }}
  SUPABASE_URL_PROD: ${{ secrets.SUPABASE_URL_PROD }}
  SUPABASE_KEY_PROD: ${{ secrets.SUPABASE_KEY_PROD }}
```

## 🐛 Troubleshooting

### **Common Issues**

#### **1. Connection Issues**

**Problem**: Cannot connect to local Supabase
**Solution**:
```bash
# Check if services are running
supabase status

# Restart services
supabase stop
supabase start

# Check Docker containers
docker ps
```

#### **2. Build Failures**

**Problem**: BuildKonfig fails to generate
**Solution**:
```bash
# Clean and rebuild
./gradlew clean
./gradlew build

# Check environment variables
echo $SUPABASE_URL_DEV
echo $SUPABASE_KEY_DEV

# Verify local.properties
cat local.properties
```

#### **3. Database Migration Issues**

**Problem**: Migrations fail to apply
**Solution**:
```bash
# Check migration status
supabase migration list

# Reset database
supabase db reset

# Verify migration files
ls -la supabase/migrations/
```

#### **4. Platform-Specific Issues**

**Android Emulator**:
```bash
# Verify network security config
# Check 10.0.2.2 is accessible
ping 10.0.2.2

# Restart emulator
emulator -avd your_avd_name
```

**iOS Simulator**:
```bash
# Verify localhost access
ping 127.0.0.1

# Check Supabase services
supabase status
```

### **Debug Mode**

#### **Enable Verbose Logging**
```kotlin
// In Supabase client configuration
fun createSupabaseClient(): SupabaseClient = createSupabaseClient(
    supabaseUrl = BuildKonfig.SUPABASE_URL,
    supabaseKey = BuildKonfig.SUPABASE_KEY
) {
    // Enable debug logging
    if (BuildKonfig.DEBUG) {
        // Add logging interceptors
    }
}
```

#### **Check Network Requests**
```bash
# Monitor network traffic
# Use browser dev tools or network monitoring tools
# Check Supabase Studio logs
```

## 📚 Best Practices

### **1. Security**
- **Enable Row Level Security (RLS)** on all tables
- **Use parameterized queries** to prevent SQL injection
- **Validate user permissions** before operations
- **Store sensitive data** in environment variables

### **2. Performance**
- **Use database indexes** for frequently queried columns
- **Implement pagination** for large result sets
- **Cache frequently accessed data** when appropriate
- **Monitor query performance** with Supabase analytics

### **3. Development Workflow**
- **Use local development** for rapid iteration
- **Version control migrations** in git
- **Test migrations locally** before deploying
- **Use seed data** for development testing

### **4. Error Handling**
- **Implement proper error handling** for all operations
- **Use Result types** for error propagation
- **Log errors appropriately** for debugging
- **Provide user-friendly error messages**

### **5. Testing**
- **Mock Supabase services** in unit tests
- **Use test database** for integration tests
- **Test authentication flows** thoroughly
- **Verify RLS policies** work correctly

## 🔗 Related Documentation

- [Analytics Integration](ANALYTICS_INTEGRATION.md) - Firebase analytics setup
- [GitHub Actions](GITHUB_ACTIONS.md) - CI/CD automation
- [Deploy Android](DEPLOY_ANDROID.md) - Android deployment
- [Deploy iOS](DEPLOY_IOS.md) - iOS deployment

## 📖 Resources

- [Supabase Documentation](https://supabase.com/docs)
- [Supabase Kotlin Client](https://github.com/supabase-community/supabase-kt)
- [Supabase CLI](https://supabase.com/docs/guides/cli)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Ktor Documentation](https://ktor.io/docs/)

## 📝 Additional Commands

### **Supabase CLI Commands**
```bash
# Project management
supabase init
supabase start
supabase stop
supabase status

# Database operations
supabase db reset
supabase db push
supabase db pull
supabase db diff

# Migration management
supabase migration new migration_name
supabase migration up
supabase migration list

# Project linking
supabase login
supabase link --project-ref your-project-id
supabase unlink
```

### **Docker Commands**
```bash
# Check running containers
docker ps

# View container logs
docker logs supabase_db_54322

# Restart specific service
docker restart supabase_db_54322

# Clean up containers
docker system prune
```

## 📋 Notes

- **Never commit `local.properties`** to version control
- **Use environment variables** for production secrets
- **Test migrations locally** before deploying to production
- **Monitor database performance** in Supabase dashboard
- **Backup important data** before major schema changes

---

**Build powerful backends with Supabase! 🚀**
