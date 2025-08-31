# Android Deployment Guide

This document covers the complete Android deployment process from development to Google Play Store distribution, including automated CI/CD workflows.

## 📋 Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Environment Setup](#environment-setup)
- [Google Play Console Setup](#google-play-console-setup)
- [Code Signing](#code-signing)
- [Release Process](#release-process)
- [CI/CD Automation](#cicd-automation)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Best Practices](#best-practices)

## 🎯 Overview

The Android deployment process uses **GitHub Actions** to automate the release workflow, ensuring consistent and reliable deployments to the Google Play Store. This guide covers the complete setup from initial configuration to automated releases.

## ✅ Prerequisites

Before starting the deployment process, ensure you have:

- **Android Studio Hedgehog** (2023.1.1) or later
- **Google Play Console account** with developer access
- **Google Cloud project** with necessary APIs enabled
- **Firebase project** for testing and analytics
- **GitHub repository** with proper secrets configured
- **Latest version of the code** checked out from repository

## 🚀 Environment Setup

### **1. Verify Project Configuration**

Check your `composeApp/build.gradle.kts` file has the correct configuration:

```kotlin
android {
    namespace = "com.yourcompany.yourapp"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.yourcompany.yourapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

### **2. Install Required Tools**

```bash
# Verify Gradle wrapper
./gradlew --version

# Check Android SDK
./gradlew androidDependencies
```

## 🔑 Google Play Console Setup

### **1. Create Google Play Console Account**

1. **Navigate to [Google Play Console](https://play.google.com/console)**
2. **Sign in with your Google account**
3. **Accept the Developer Distribution Agreement**
4. **Pay the one-time $25 registration fee**

### **2. Create New App**

1. **Click "Create app"**
2. **Enter app details**:
   - App name: Your app name
   - Default language: English
   - App or game: App
   - Free or paid: Choose based on your model
3. **Click "Create app"**

### **3. Complete Store Listing**

1. **App content**:
   - App description
   - Short description
   - Graphics (icon, screenshots, feature graphic)
   - Content rating
   - Privacy policy

2. **App release**:
   - Production track
   - Internal testing track
   - Closed testing track

## 🔐 Code Signing

### **1. Generate Keystore**

#### **Create New Keystore**
```bash
# Generate keystore using keytool
keytool -genkey -v -keystore my-release-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
```

#### **Store Keystore Securely**
- **Keep keystore file safe** - losing it means you can't update your app
- **Remember passwords** for keystore and key alias
- **Backup keystore** in multiple secure locations

### **2. Configure Signing in Build**

#### **Add Signing Config**
```kotlin
// In composeApp/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/your/keystore.jks")
            storePassword = "your-keystore-password" # pragma: allowlist secret
keyAlias = "your-key-alias" # pragma: allowlist secret
keyPassword = "your-key-password" # pragma: allowlist secret
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

#### **Use Environment Variables (Recommended)**
```kotlin
// In composeApp/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "debug.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "android"
            keyAlias = System.getenv("KEY_ALIAS") ?: "androiddebugkey"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "android"
        }
    }
}
```

### **3. Configure GitHub Secrets**

#### **Convert Keystore to Base64**
```bash
# Convert keystore to base64
base64 -i my-release-key.keystore -o keystore_base64.txt

# Copy content to GitHub secret
cat keystore_base64.txt
```

#### **Add GitHub Secrets**
1. **Go to your GitHub repository**
2. **Navigate to Settings > Secrets and variables > Actions**
3. **Add the following secrets**:
   - `ANDROID_KEYSTORE`: Content of `keystore_base64.txt`
   - `KEYSTORE_PASSWORD`: Your keystore password
   - `ANDROID_KEY_ALIAS`: Your key alias
   - `ANDROID_KEY_PASSWORD`: Your key password

## 🚀 Release Process

### **1. Manual Release Process**

#### **Generate Signed Bundle**
```bash
# Generate signed APK
./gradlew assembleRelease

# Generate signed bundle (recommended for Play Store)
./gradlew bundleRelease
```

#### **Upload to Play Console**
1. **Log in to Google Play Console**
2. **Navigate to your app**
3. **Go to Production > Create new release**
4. **Upload your signed bundle**
5. **Add release notes**
6. **Review and roll out**

### **2. Automated Release Process**

The project includes GitHub Actions workflows that automate the entire release process.

#### **Trigger Automated Release**
```bash
# Push to main branch triggers automated release
git push origin main
```

#### **What Happens Automatically**
1. **Build signed APK and bundle**
2. **Run tests on Firebase Test Lab**
3. **Generate coverage reports**
4. **Upload to Play Store**
5. **Create GitHub release**

## ⚡ CI/CD Automation

### **GitHub Actions Workflow**

The automated deployment uses the workflow in `.github/workflows/android_deploy.yml`.

#### **Workflow Steps**
1. **Setup Environment**
   - Checkout code
   - Setup Java and Android SDK
   - Load Google Services configuration

2. **Build and Test**
   - Generate signed APK and bundle
   - Run unit tests
   - Generate coverage reports

3. **Firebase Test Lab**
   - Run instrumentation tests
   - Test on multiple devices
   - Generate test reports

4. **Deploy to Play Store**
   - Upload signed bundle
   - Update release notes
   - Publish to production track

### **Configuration Variables**

The workflow is fully configurable with variables at the top:

```yaml
env:
  # Project Configuration
  PROJECT_NAME: "YourProjectName"
  ANDROID_APP_MODULE: "composeApp"
  
  # Android Configuration
  ANDROID_PACKAGE_NAME: "com.yourcompany.yourapp"
  ANDROID_NAMESPACE: "com.yourcompany.yourapp"
  
  # Build Configuration
  JAVA_VERSION: "17"
  GRADLE_WRAPPER: "./gradlew"
  
  # Firebase Test Lab
  FIREBASE_DEVICE_MODEL: "shiba"
  FIREBASE_DEVICE_VERSION: "34"
  
  # Play Store
  PLAY_STORE_TRACK: "internal"
  RELEASE_NOTES_DIR: "composeApp/release/whatsNew"
```

### **Required Secrets**

Ensure these secrets are configured in your GitHub repository:

#### **Code Signing**
- `ANDROID_KEYSTORE`: Base64 encoded keystore
- `KEYSTORE_PASSWORD`: Keystore password
- `ANDROID_KEY_ALIAS`: Key alias
- `ANDROID_KEY_PASSWORD`: Key password

#### **Google Services**
- `GOOGLE_SERVICES_JSON`: Base64 encoded google-services.json
- `GOOGLE_PROJECT_ID`: Google Cloud project ID
- `GOOGLE_SERVICE_ACCOUNT`: Service account JSON key

#### **Play Store**
- `PLAY_STORE_CONFIG`: Play Store configuration (if using service account)

## 🧪 Testing

### **1. Local Testing**

#### **Run Tests Locally**
```bash
# Unit tests
./gradlew :composeApp:testDebugUnitTest

# Instrumented tests
./gradlew :composeApp:connectedDebugAndroidTest

# Build variants
./gradlew assembleDebug
./gradlew assembleRelease
```

#### **Test Signing Configuration**
```bash
# Test release build
./gradlew assembleRelease

# Verify APK is signed
jarsigner -verify -verbose -certs app-release.apk
```

### **2. Firebase Test Lab**

#### **Configure Firebase Test Lab**
1. **Enable Firebase Test Lab** in your Firebase project
2. **Configure test devices** in the workflow
3. **Set up test targets** for different device configurations

#### **Test Device Configuration**
```yaml
# In workflow configuration
FIREBASE_DEVICE_MODEL: "shiba"      # Pixel 4
FIREBASE_DEVICE_VERSION: "34"       # Android 14
FIREBASE_LOCALE: "en"               # English
FIREBASE_ORIENTATION: "portrait"    # Portrait orientation
```

### **3. Coverage Reports**

#### **Generate Coverage**
```bash
# Generate coverage report
./gradlew :composeApp:testDebugUnitTestCoverage

# View coverage in browser
open composeApp/build/reports/coverage/debug/index.html
```

## 🐛 Troubleshooting

### **Common Issues**

#### **1. Code Signing Issues**

**Problem**: Build fails with signing errors
**Solution**:
```bash
# Verify keystore file exists
ls -la path/to/your/keystore.jks

# Check keystore validity
keytool -list -v -keystore path/to/your/keystore.jks

# Verify passwords in build.gradle.kts
```

#### **2. Build Failures**

**Problem**: Build fails during compilation
**Solution**:
```bash
# Clean project
./gradlew clean

# Check dependencies
./gradlew :composeApp:dependencies

# Verify SDK installation
./gradlew androidDependencies
```

#### **3. Play Store Upload Issues**

**Problem**: Bundle upload fails
**Solution**:
1. **Verify bundle signing** is correct
2. **Check Play Console** for specific error messages
3. **Ensure app meets** Play Store requirements
4. **Verify version code** is higher than previous release

#### **4. GitHub Actions Failures**

**Problem**: Workflow fails in CI/CD
**Solution**:
1. **Check workflow logs** for specific errors
2. **Verify all secrets** are properly configured
3. **Check file paths** in workflow configuration
4. **Ensure permissions** are set correctly

### **Debug Mode**

#### **Enable Verbose Logging**
```bash
# Gradle debug
./gradlew assembleRelease --debug

# Check specific task
./gradlew :composeApp:assembleRelease --info
```

#### **Verify Configuration**
```bash
# Check signing config
./gradlew signingReport

# Verify build variants
./gradlew :composeApp:assembleRelease --dry-run
```

## 📚 Best Practices

### **1. Release Management**
- **Use semantic versioning** for version names
- **Increment version code** with each release
- **Test thoroughly** before releasing
- **Maintain release notes** for users

### **2. Code Signing**
- **Store keystore securely** with multiple backups
- **Use environment variables** for passwords
- **Test signing configuration** locally
- **Document keystore details** for team

### **3. Testing Strategy**
- **Run tests locally** before pushing
- **Use Firebase Test Lab** for device testing
- **Maintain high test coverage** (90%+)
- **Test on multiple device types** and API levels

### **4. CI/CD Pipeline**
- **Automate repetitive tasks** with GitHub Actions
- **Use feature flags** for gradual rollouts
- **Monitor deployment success rates**
- **Maintain deployment logs** for troubleshooting

### **5. Security**
- **Never commit keystore files** to version control
- **Use GitHub secrets** for sensitive information
- **Rotate service account keys** regularly
- **Monitor Play Console** for security issues

## 🔗 Related Documentation

- [GitHub Actions Workflows](GITHUB_ACTIONS.md) - CI/CD automation
- [Firebase Integration](FIREBASE_INTEGRATION.md) - Testing and analytics
- [Kotlin Format & Lint](KOTLIN_FORMAT_LINT.md) - Code quality tools
- [Code Coverage Reports](CODE_COVERAGE_REPORTS.md) - Testing coverage

## 📖 Resources

- [Google Play Console](https://play.google.com/console)
- [Android App Bundle](https://developer.android.com/guide/app-bundle)
- [Firebase Test Lab](https://firebase.google.com/docs/test-lab)
- [GitHub Actions](https://github.com/features/actions)

## 📝 Additional Commands

### **Useful Gradle Commands**
```bash
# Build variants
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew bundleRelease

# Testing
./gradlew test
./gradlew connectedAndroidTest
./gradlew testDebugUnitTestCoverage

# Dependencies
./gradlew dependencies
./gradlew :composeApp:dependencies

# Clean and rebuild
./gradlew clean
./gradlew clean build
```

### **Keystore Management**
```bash
# List keystore contents
keytool -list -v -keystore your-keystore.jks

# Change keystore password
keytool -storepasswd -keystore your-keystore.jks

# Change key password
keytool -keypasswd -alias your-alias -keystore your-keystore.jks
```

## 📋 Notes

- **Keystore files should never be committed** to version control
- **Use App Bundles (.aab)** instead of APKs for Play Store
- **Version code must be incremented** with each release
- **Test on multiple device types** before releasing
- **Monitor Play Console** for user feedback and issues

---

**Deploy Android apps reliably with automated CI/CD! 🚀**