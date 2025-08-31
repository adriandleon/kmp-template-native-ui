# GitHub Actions Workflows

This project includes three optimized, configurable GitHub Actions workflows that can be easily reused across different projects. All workflows are designed with configurable variables at the top for easy customization.

## 📋 Available Workflows

### 1. **Shared Test & Lint** (`.github/workflows/shared_test_lint.yml`)
- **Trigger**: Pull Requests
- **Purpose**: Code quality checks, formatting validation, and unit tests
- **Jobs**: Danger checks, Kotlin static analysis, Swift static analysis, unit tests

### 2. **Android Deploy** (`.github/workflows/android_deploy.yml`)
- **Trigger**: Push to main branch
- **Purpose**: Android app building, testing, and deployment
- **Jobs**: APK generation, Firebase Test Lab, coverage reports, Play Store deployment

### 3. **iOS Deploy** (`.github/workflows/ios_deploy.yml`)
- **Trigger**: Push to main branch
- **Purpose**: iOS app testing and deployment
- **Jobs**: iOS unit tests, TestFlight deployment

## ⚙️ Configuration Variables

All workflows use configurable variables at the top for easy project customization. Here's what you need to update when migrating to a new project:

### **Project Configuration**
```yaml
# Project Configuration
PROJECT_NAME: "YourProjectName"
ANDROID_APP_MODULE: "app"              # Your Android module name
IOS_APP_MODULE: "iosApp"               # Your iOS module name
SHARED_MODULE: "shared"                # Your shared module name
```

### **Android Configuration**
```yaml
# Android Configuration
ANDROID_PACKAGE_NAME: "com.yourcompany.yourapp"
ANDROID_GOOGLE_SERVICES_FILE: "google-services.json"
ANDROID_NAMESPACE: "com.yourcompany.yourapp"
```

### **iOS Configuration**
```yaml
# iOS Configuration
IOS_BUNDLE_ID: "com.yourcompany.YourApp"
IOS_GOOGLE_SERVICES_FILE: "GoogleService-Info.plist"
IOS_APP_FOLDER: "YourApp"             # Your iOS app folder name
IOS_TARGET_NAME: "YourApp"            # Your iOS target name
```

### **Build Configuration**
```yaml
# Build Configuration
JAVA_VERSION: "17"                     # Java version for Android builds
RUBY_VERSION: "3.3"                    # Ruby version for iOS builds
GRADLE_WRAPPER: "./gradlew"            # Gradle wrapper path
XCODE_VERSION: "latest-stable"         # Xcode version for iOS builds
```

### **Deployment Configuration**
```yaml
# Firebase Test Lab Configuration
FIREBASE_DEVICE_MODEL: "shiba"
FIREBASE_DEVICE_VERSION: "34"
FIREBASE_LOCALE: "en"                  # Change to your target locale
FIREBASE_ORIENTATION: "portrait"

# Play Store Configuration
PLAY_STORE_TRACK: "internal"
RELEASE_NOTES_DIR: "app/release/whatsNew"  # Update path to match your structure
```

## 🚀 Quick Migration Guide

### **Step 1: Copy Workflows and Configuration**
Copy the following files to your new project:
- Three workflow files from `.github/workflows/`
- `config/Dangerfile.df.kts` for PR automation

### **Step 2: Update Project Variables**
Update the configuration variables at the top of each workflow file:

```yaml
# Example: From Template to MyApp
PROJECT_NAME: "Template" → "MyApp"
ANDROID_PACKAGE_NAME: "com.adriandeleon.template" → "com.mycompany.myapp"
IOS_BUNDLE_ID: "com.adriandeleon.Template" → "com.mycompany.MyApp"
ANDROID_APP_MODULE: "composeApp" → "app"  # If different module name
IOS_APP_FOLDER: "Template" → "MyApp"      # If different folder name
```

### **Step 3: Update File Paths and Dangerfile**
Ensure file paths match your project structure:

```yaml
# Update these if your project has different structure
GRADLE_PATH: "app/build.gradle.kts"        # Instead of "composeApp/build.gradle.kts"
RELEASE_NOTES_DIR: "app/release/whatsNew"  # Instead of "composeApp/release/whatsNew"
```

**Update Dangerfile Configuration:**
```kotlin
// Project Configuration
val PROJECT_NAME = "YourProjectName"                    // Your project name
val PROJECT_OWNER = "yourusername"                      // GitHub username/organization
val PROJECT_REPO = "your-repo-name"                     // Repository name

// Module Paths - Update these to match your project structure
val SHARED_MODULE_PATH = "shared/src/commonMain/"       // Path to shared module
val ANDROID_MODULE_PATH = "app/src/main/"               // Path to Android module
val IOS_MODULE_PATH = "iosApp/YourApp/"                 // Path to iOS module
```

### **Step 4: Set Required Secrets**
Configure the following secrets in your GitHub repository:

#### **Required for All Workflows:**
- `SUPABASE_URL_PROD` - Supabase production URL
- `SUPABASE_KEY_PROD` - Supabase production key
- `GOOGLE_SERVICES_JSON` - Android Google Services JSON (base64 encoded)
- `GOOGLE_SERVICES_PLIST` - iOS Google Services PLIST (base64 encoded)

#### **Required for Android Deploy:**
- `GOOGLE_PROJECT_ID` - Google Cloud project ID
- `GOOGLE_SERVICE_ACCOUNT` - Google Cloud service account JSON
- `ANDROID_KEYSTORE` - Android release keystore (base64 encoded)
- `KEYSTORE_PASSWORD` - Keystore password
- `ANDROID_KEY_ALIAS` - Key alias
- `ANDROID_KEY_PASSWORD` - Key password
- `CODECOV_TOKEN` - Codecov token for coverage reports

#### **Required for iOS Deploy:**
- `APPSTORE_KEY_ID` - App Store Connect API key ID
- `APPSTORE_ISSUER_ID` - App Store Connect issuer ID
- `APPSTORE_PRIVATE_KEY` - App Store Connect private key
- `MATCH_GIT_BASIC_AUTHORIZATION` - Match git authorization
- `MATCH_PASSPHRASE` - Match passphrase

## 🔧 Workflow Details

### **Shared Test & Lint Workflow**

**Jobs:**
1. **Danger Checks** - PR metadata validation and quality checks
2. **Kotlin Static Analysis** - Code formatting and Detekt analysis
3. **Swift Static Analysis** - SwiftFormat and SwiftLint checks
4. **Unit Tests** - Konsist tests and shared module tests

**Danger Configuration:**
The Danger checks are powered by a configurable `config/Dangerfile.df.kts` that automatically:
- Validates PR descriptions and size
- Suggests appropriate labels based on modified files
- Checks release notes requirements
- Provides helpful feedback to contributors

See [Pull Request Checks](PR_DANGER_CHECKS.md) for detailed configuration options.

**Key Features:**
- Automatic code quality checks on every PR
- Configurable tool paths and task names
- Integrated with pre-commit hooks
- Coverage reporting for pull requests

### **Android Deploy Workflow**

**Jobs:**
1. **Generate APK** - Build debug APK and test APK
2. **Firebase Test Lab** - Run instrumentation tests
3. **Coverage Report** - Generate and upload coverage reports
4. **Distribute** - Build, sign, and deploy to Play Store

**Key Features:**
- Automatic version bumping
- Firebase Test Lab integration
- Play Store deployment
- Configurable device testing parameters

### **iOS Deploy Workflow**

**Jobs:**
1. **iOS Tests** - Unit tests and TestFlight deployment

**Key Features:**
- Xcode version management
- Ruby environment setup
- Fastlane integration
- Configurable iOS target settings

## 📝 Customization Examples

### **Adding New Build Variants**
```yaml
# Add new build variants to Android workflow
ANDROID_BUILD_VARIANTS: "debug,release,staging"
```

### **Custom Firebase Test Devices**
```yaml
# Update Firebase test configuration
FIREBASE_DEVICE_MODEL: "redfin"           # Pixel 5
FIREBASE_DEVICE_VERSION: "30"             # Android 11
FIREBASE_LOCALE: "en-US"                  # US English
```

### **Multiple iOS Targets**
```yaml
# Support multiple iOS targets
IOS_TARGET_NAMES: "App,AppTests,AppUITests"
```

### **Custom Danger Rules**
```kotlin
// Add new language support in Dangerfile.df.kts
val RELEASE_NOTES_LANGUAGES = mapOf(
    "en-US" to "whatsnew-en-US",                        // English (US)
    "es-419" to "whatsnew-es-419",                      // Spanish (Latin America)
    "pt-BR" to "whatsnew-pt-BR",                        // Portuguese (Brazil)
    "fr-FR" to "whatsnew-fr-FR"                         // French (France)
)

// Disable specific checks
val ENABLE_PR_SIZE_WARNING = false                       // Disable PR size warnings
val ENABLE_LABEL_CHECKS = false                          // Disable label requirement checks
```

## 🐛 Troubleshooting

### **Common Issues:**

1. **Workflow not triggering**
   - Check branch names in `on.push.branches`
   - Verify workflow file is in `.github/workflows/`

2. **Build failures**
   - Verify all required secrets are set
   - Check file paths match your project structure
   - Ensure Gradle wrapper and Java versions are correct

3. **Cache issues**
   - Cache keys are automatically generated based on file hashes
   - No manual configuration needed

### **Debug Mode:**
Add `--debug` flag to Gradle commands for verbose output:

```yaml
run: ${{ env.GRADLE_WRAPPER }} assembleDebug --debug
```

## 🔗 Related Documentation

- [Pre-Commit Hooks](PRE_COMMIT_HOOKS.md) - Local code quality checks
- [Kotlin Format & Lint](KOTLIN_FORMAT_LINT.md) - Kotlin code quality tools
- [Swift Format & Lint](SWIFT_FORMAT_LINT.md) - Swift code quality tools
- [Code Coverage Reports](CODE_COVERAGE_REPORTS.md) - Coverage configuration
- [Pull Request Checks](PR_DANGER_CHECKS.md) - PR validation rules

## 📚 Best Practices

1. **Keep variables at the top** for easy maintenance
2. **Use descriptive variable names** that clearly indicate their purpose
3. **Group related variables** with clear section headers
4. **Document any project-specific requirements** in comments
5. **Test workflows** in a fork before applying to main project
6. **Version control workflows** alongside your project code

---

**Happy automating! 🚀**
