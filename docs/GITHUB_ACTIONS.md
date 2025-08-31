# GitHub Actions Workflows

This document covers the complete GitHub Actions CI/CD setup for the Kotlin Multiplatform project, including automated testing, building, and deployment workflows for both Android and iOS platforms.

## 📋 Table of Contents

- [Overview](#overview)
- [What are GitHub Actions?](#what-are-github-actions)
- [Available Workflows](#available-workflows)
- [Configuration](#configuration)
- [Workflow Details](#workflow-details)
- [Customization](#customization)
- [Secrets Management](#secrets-management)
- [Troubleshooting](#troubleshooting)
- [Best Practices](#best-practices)

## 🎯 Overview

GitHub Actions provides automated CI/CD workflows that build, test, and deploy your Kotlin Multiplatform app across all platforms. The workflows are fully configurable and designed for easy migration to other repositories with minimal changes.

## ⚡ What are GitHub Actions?

GitHub Actions is a continuous integration and continuous deployment (CI/CD) platform that allows you to automate your build, test, and deployment pipeline. Key features include:

- **Automated Workflows**: Trigger builds on code changes
- **Multi-Platform Support**: Build for Android, iOS, and shared modules
- **Parallel Execution**: Run multiple jobs simultaneously
- **Environment Management**: Handle different build environments
- **Secrets Management**: Secure handling of sensitive information
- **Artifact Sharing**: Pass build outputs between jobs

## 🚀 Available Workflows

### **1. Shared Module Testing & Linting**

**File**: `.github/workflows/shared_test_lint.yml`

**Purpose**: Tests and validates the shared Kotlin Multiplatform module

**Triggers**:
- Push to `main` branch
- Pull request to `main` branch
- Manual workflow dispatch

**Jobs**:
- **Test Shared Module**: Run unit tests across all platforms
- **Lint Code**: Run Detekt static analysis
- **Format Check**: Verify code formatting with Ktfmt

### **2. Android App Deployment**

**File**: `.github/workflows/android_deploy.yml`

**Purpose**: Builds, tests, and deploys the Android app to Google Play Store

**Triggers**:
- Push to `main` branch
- Manual workflow dispatch

**Jobs**:
- **Build & Test**: Compile app and run tests
- **Firebase Test Lab**: Run instrumentation tests on real devices
- **Deploy to Play Store**: Upload signed bundle to production track

### **3. iOS App Deployment**

**File**: `.github/workflows/ios_deploy.yml`

**Purpose**: Builds, tests, and deploys the iOS app to TestFlight

**Triggers**:
- Push to `main` branch
- Manual workflow dispatch

**Jobs**:
- **Build & Test**: Compile app and run tests
- **Upload to TestFlight**: Deploy to Apple's testing platform

## ⚙️ Configuration

### **Workflow Customization**

All workflows are fully configurable with variables at the top of each file:

```yaml
# Example configuration section
env:
  # Project Configuration
  PROJECT_NAME: "YourProjectName"
  ANDROID_APP_MODULE: "composeApp"
  IOS_APP_MODULE: "iosApp"
  
  # Android Configuration
  ANDROID_PACKAGE_NAME: "com.yourcompany.yourapp"
  ANDROID_NAMESPACE: "com.yourcompany.yourapp"
  
  # iOS Configuration
  IOS_BUNDLE_ID: "com.yourcompany.yourapp"
  IOS_TEAM_ID: "XXXXXXXXXX"
  
  # Build Configuration
  JAVA_VERSION: "17"
  GRADLE_WRAPPER: "./gradlew"
  XCODE_VERSION: "15.2"
  
  # Testing Configuration
  FIREBASE_DEVICE_MODEL: "shiba"
  FIREBASE_DEVICE_VERSION: "34"
  
  # Deployment Configuration
  PLAY_STORE_TRACK: "internal"
  RELEASE_NOTES_DIR: "composeApp/release/whatsNew"
```

### **Quick Migration Guide**

To migrate these workflows to a new repository:

1. **Copy workflow files** to `.github/workflows/`
2. **Update configuration variables** at the top of each file
3. **Configure GitHub secrets** for your project
4. **Update file paths** if your structure differs
5. **Test workflows** with a small change

## 🔧 Workflow Details

### **Shared Module Testing & Linting**

#### **Workflow Steps**

1. **Setup Environment**
   ```yaml
   - name: Setup Java
     uses: actions/setup-java@v4
     with:
       java-version: ${{ env.JAVA_VERSION }}
       distribution: 'temurin'
   ```

2. **Checkout Code**
   ```yaml
   - name: Checkout
     uses: actions/checkout@v4
     with:
       fetch-depth: 0
   ```

3. **Setup Gradle Cache**
   ```yaml
   - name: Setup Gradle Cache
     uses: actions/cache@v4
     with:
       path: ~/.gradle/caches
       key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
       restore-keys: |
         ${{ runner.os }}-gradle-
   ```

4. **Run Tests**
   ```yaml
   - name: Test Shared Module
     run: ${{ env.GRADLE_WRAPPER }} :shared:test
   ```

5. **Run Linting**
   ```yaml
   - name: Lint Code
     run: ${{ env.GRADLE_WRAPPER }} :shared:detektMain
   ```

6. **Check Formatting**
   ```yaml
   - name: Check Formatting
     run: ${{ env.GRADLE_WRAPPER }} :shared:ktfmtCheck
   ```

#### **Configuration Variables**

```yaml
env:
  # Build Configuration
  JAVA_VERSION: "17"
  GRADLE_WRAPPER: "./gradlew"
  
  # Project Paths
  SHARED_MODULE_PATH: "shared"
  COMPOSE_APP_MODULE_PATH: "composeApp"
  IOS_APP_MODULE_PATH: "iosApp"
```

### **Android App Deployment**

#### **Workflow Steps**

1. **Setup Environment**
   ```yaml
   - name: Setup Java
     uses: actions/setup-java@v4
     with:
       java-version: ${{ env.JAVA_VERSION }}
       distribution: 'temurin'
   
   - name: Setup Android SDK
     uses: android-actions/setup-android@v3
   ```

2. **Load Configuration**
   ```yaml
   - name: Load Google Services JSON
     run: echo ${{ secrets.GOOGLE_SERVICES_JSON }} | base64 -di > ${{ env.COMPOSE_APP_MODULE_PATH }}/google-services.json
   ```

3. **Setup Signing**
   ```yaml
   - name: Setup Keystore
     run: |
       echo ${{ secrets.ANDROID_KEYSTORE }} | base64 -di > keystore.jks
       echo "storeFile=keystore.jks" >> ${{ env.COMPOSE_APP_MODULE_PATH }}/keystore.properties
       echo "storePassword=${{ secrets.KEYSTORE_PASSWORD }}" >> ${{ env.COMPOSE_APP_MODULE_PATH }}/keystore.properties
       echo "keyAlias=${{ secrets.ANDROID_KEY_ALIAS }}" >> ${{ env.COMPOSE_APP_MODULE_PATH }}/keystore.properties
       echo "keyPassword=${{ secrets.ANDROID_KEY_PASSWORD }}" >> ${{ env.COMPOSE_APP_MODULE_PATH }}/keystore.properties
   ```

4. **Build & Test**
   ```yaml
   - name: Build and Test
     run: |
       ${{ env.GRADLE_WRAPPER }} :${{ env.COMPOSE_APP_MODULE_PATH }}:assembleRelease
       ${{ env.GRADLE_WRAPPER }} :${{ env.COMPOSE_APP_MODULE_PATH }}:testReleaseUnitTest
       ${{ env.GRADLE_WRAPPER }} :${{ env.COMPOSE_APP_MODULE_PATH }}:bundleRelease
   ```

5. **Firebase Test Lab**
   ```yaml
   - name: Run Firebase Test Lab
     run: |
       gcloud firebase test android run \
         --type instrumentation \
         --app ${{ env.COMPOSE_APP_MODULE_PATH }}/build/outputs/apk/release/${{ env.COMPOSE_APP_MODULE_PATH }}-release.apk \
         --test ${{ env.COMPOSE_APP_MODULE_PATH }}/build/outputs/apk/androidTest/release/${{ env.COMPOSE_APP_MODULE_PATH }}-release-androidTest.apk \
         --device model=${{ env.FIREBASE_DEVICE_MODEL }},version=${{ env.FIREBASE_DEVICE_VERSION }},locale=${{ env.FIREBASE_LOCALE }},orientation=${{ env.FIREBASE_ORIENTATION }} \
         --timeout ${{ env.FIREBASE_TIMEOUT }}
   ```

6. **Deploy to Play Store**
   ```yaml
   - name: Deploy to Play Store
     run: |
       ${{ env.GRADLE_WRAPPER }} :${{ env.COMPOSE_APP_MODULE_PATH }}:publishReleaseBundle
   ```

#### **Configuration Variables**

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
  FIREBASE_LOCALE: "en"
  FIREBASE_ORIENTATION: "portrait"
  FIREBASE_TIMEOUT: "30m"
  
  # Play Store
  PLAY_STORE_TRACK: "internal"
  RELEASE_NOTES_DIR: "composeApp/release/whatsNew"
```

### **iOS App Deployment**

#### **Workflow Steps**

1. **Setup Environment**
   ```yaml
   - name: Setup Xcode
     uses: maxim-lobanov/setup-xcode@v1
     with:
       xcode-version: ${{ env.XCODE_VERSION }}
   ```

2. **Load Configuration**
   ```yaml
   - name: Load Google Services PLIST
     run: echo ${{ secrets.GOOGLE_SERVICES_PLIST }} | base64 -di > ${{ env.IOS_APP_MODULE_PATH }}/Template/GoogleService-Info.plist
   ```

3. **Setup Code Signing**
   ```yaml
   - name: Setup Code Signing
     uses: apple-actions/import-codesigning-certs@v1
     with:
       p12-file-base64: ${{ secrets.IOS_P12_CERTIFICATE }}
       p12-password: ${{ secrets.IOS_P12_PASSWORD }}
   ```

4. **Build & Test**
   ```yaml
   - name: Build and Test
     run: |
       xcodebuild -workspace ${{ env.IOS_APP_MODULE_PATH }}/Template.xcworkspace \
         -scheme Template \
         -configuration Release \
         -destination 'platform=iOS Simulator,name=iPhone 16,OS=latest' \
         clean build test
   ```

5. **Upload to TestFlight**
   ```yaml
   - name: Upload to TestFlight
     run: |
       xcrun altool --upload-app \
         --type ios \
         --file ${{ env.IOS_APP_MODULE_PATH }}/build/Template.ipa \
         --username ${{ secrets.APPLE_ID }} \
         --password ${{ secrets.APP_SPECIFIC_PASSWORD }}
   ```

#### **Configuration Variables**

```yaml
env:
  # Project Configuration
  PROJECT_NAME: "YourProjectName"
  IOS_APP_MODULE: "iosApp"
  
  # iOS Configuration
  IOS_BUNDLE_ID: "com.yourcompany.yourapp"
  IOS_TEAM_ID: "XXXXXXXXXX"
  
  # Build Configuration
  XCODE_VERSION: "15.2"
  IOS_SIMULATOR: "iPhone 16"
  IOS_OS_VERSION: "latest"
  
  # TestFlight
  TESTFLIGHT_UPLOAD: "true"
  TESTFLIGHT_NOTES: "Automated build from CI/CD"
```

## 🎨 Customization

### **Workflow Customization Examples**

#### **Change Build Environment**
```yaml
# Use different Java version
env:
  JAVA_VERSION: "21"  # Instead of "17"

# Use different Xcode version
env:
  XCODE_VERSION: "16.0"  # Instead of "15.2"
```

#### **Modify Test Configuration**
```yaml
# Change Firebase Test Lab device
env:
  FIREBASE_DEVICE_MODEL: "redfin"      # Pixel 5 instead of "shiba"
  FIREBASE_DEVICE_VERSION: "33"        # Android 13 instead of "34"

# Change iOS simulator
env:
  IOS_SIMULATOR: "iPhone 15 Pro"       # Instead of "iPhone 16"
```

#### **Customize Deployment**
```yaml
# Change Play Store track
env:
  PLAY_STORE_TRACK: "alpha"            # Instead of "internal"

# Change TestFlight upload
env:
  TESTFLIGHT_UPLOAD: "false"           # Disable TestFlight upload
```

### **Adding New Workflows**

To add a new workflow:

1. **Create new file** in `.github/workflows/`
2. **Use existing workflow** as template
3. **Update configuration variables** for your use case
4. **Add necessary secrets** to repository
5. **Test workflow** with manual dispatch

#### **Example: Custom Workflow**
```yaml
name: Custom Workflow

on:
  workflow_dispatch:
    inputs:
      environment:
        description: 'Environment to deploy to'
        required: true
        default: 'staging'
        type: choice
        options:
        - staging
        - production

env:
  PROJECT_NAME: "YourProjectName"
  DEPLOY_ENVIRONMENT: ${{ github.event.inputs.environment }}

jobs:
  custom-job:
    runs-on: ubuntu-latest
    steps:
      - name: Custom Step
        run: echo "Deploying to ${{ env.DEPLOY_ENVIRONMENT }}"
```

## 🔐 Secrets Management

### **Required Secrets**

#### **Android Deployment**
```yaml
# Code Signing
ANDROID_KEYSTORE: "base64_encoded_keystore" # pragma: allowlist secret
KEYSTORE_PASSWORD: "keystore_password" # pragma: allowlist secret
ANDROID_KEY_ALIAS: "key_alias" # pragma: allowlist secret
ANDROID_KEY_PASSWORD: "key_password" # pragma: allowlist secret

# Google Services
GOOGLE_SERVICES_JSON: "base64_encoded_google_services_json" # pragma: allowlist secret
GOOGLE_PROJECT_ID: "firebase_project_id" # pragma: allowlist secret
GOOGLE_SERVICE_ACCOUNT: "base64_encoded_service_account_json" # pragma: allowlist secret
```

#### **iOS Deployment**
```yaml
# Code Signing
IOS_P12_CERTIFICATE: "base64_encoded_p12_certificate" # pragma: allowlist secret
IOS_P12_PASSWORD: "p12_password" # pragma: allowlist secret
IOS_PROVISIONING_PROFILE: "base64_encoded_provisioning_profile" # pragma: allowlist secret

# App Store Connect
APPLE_ID: "your_apple_id@email.com" # pragma: allowlist secret
APP_SPECIFIC_PASSWORD: "app_specific_password" # pragma: allowlist secret

# Google Services
GOOGLE_SERVICES_PLIST: "base64_encoded_google_service_info_plist" # pragma: allowlist secret
```

### **Adding Secrets**

1. **Go to your GitHub repository**
2. **Navigate to Settings > Secrets and variables > Actions**
3. **Click "New repository secret"**
4. **Enter secret name and value**
5. **Click "Add secret"**

### **Converting Files to Base64**

```bash
# Convert keystore to base64
base64 -i your-keystore.jks -o keystore_base64.txt

# Convert Google Services JSON
base64 -i google-services.json -o google_services_base64.txt

# Convert P12 certificate
base64 -i certificate.p12 -o certificate_base64.txt
```

## 🐛 Troubleshooting

### **Common Issues**

#### **1. Workflow Failures**

**Problem**: Workflow fails during execution
**Solution**:
1. **Check workflow logs** for specific error messages
2. **Verify all secrets** are properly configured
3. **Check file paths** in workflow configuration
4. **Ensure permissions** are set correctly

#### **2. Build Failures**

**Problem**: Build step fails
**Solution**:
```bash
# Test build locally first
./gradlew assembleRelease

# Check dependencies
./gradlew dependencies

# Verify configuration
./gradlew signingReport
```

#### **3. Test Failures**

**Problem**: Tests fail in CI/CD
**Solution**:
1. **Run tests locally** to reproduce issue
2. **Check test configuration** and dependencies
3. **Verify test data** and mock objects
4. **Check platform-specific** test setup

#### **4. Deployment Failures**

**Problem**: Deployment to store fails
**Solution**:
1. **Verify credentials** and permissions
2. **Check app configuration** (version, bundle ID)
3. **Ensure app meets** store requirements
4. **Review store console** for specific errors

### **Debug Mode**

#### **Enable Debug Logging**
```yaml
# Add to workflow step
- name: Debug Information
  run: |
    echo "Project: ${{ env.PROJECT_NAME }}"
    echo "Module: ${{ env.ANDROID_APP_MODULE }}"
    echo "Package: ${{ env.ANDROID_PACKAGE_NAME }}"
    ls -la
```

#### **Manual Workflow Dispatch**
```yaml
# Add to workflow triggers
on:
  workflow_dispatch:
    inputs:
      debug:
        description: 'Enable debug mode'
        required: false
        default: 'false'
        type: boolean
```

## 📚 Best Practices

### **1. Workflow Organization**
- **Keep workflows focused** on specific tasks
- **Use descriptive names** for jobs and steps
- **Group related steps** in logical jobs
- **Avoid duplication** between workflows

### **2. Configuration Management**
- **Use environment variables** for configuration
- **Keep sensitive data** in GitHub secrets
- **Document all variables** and their purpose
- **Use consistent naming** conventions

### **3. Error Handling**
- **Add proper error handling** to critical steps
- **Use conditional execution** for optional steps
- **Provide clear error messages** in logs
- **Implement retry logic** for flaky operations

### **4. Performance Optimization**
- **Use caching** for dependencies and build artifacts
- **Run jobs in parallel** when possible
- **Optimize build steps** to minimize execution time
- **Use appropriate runners** for different job types

### **5. Security**
- **Never commit secrets** to version control
- **Use least privilege** for service accounts
- **Rotate credentials** regularly
- **Monitor workflow access** and permissions

## 🔗 Related Documentation

- [Deploy Android](DEPLOY_ANDROID.md) - Android deployment details
- [Deploy iOS](DEPLOY_IOS.md) - iOS deployment details
- [Firebase Integration](FIREBASE_INTEGRATION.md) - Firebase setup and usage
- [PR Danger Checks](PR_DANGER_CHECKS.md) - Automated PR validation

## 📖 Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Workflow Syntax Reference](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [GitHub Actions Examples](https://github.com/actions/starter-workflows)
- [Android Actions](https://github.com/android-actions/setup-android)
- [Xcode Actions](https://github.com/maxim-lobanov/setup-xcode)

## 📝 Additional Commands

### **Workflow Management**
```bash
# List workflows
gh workflow list

# View workflow runs
gh run list

# Rerun failed workflow
gh run rerun <run-id>

# Download workflow logs
gh run download <run-id>
```

### **Local Testing**
```bash
# Test Android build
./gradlew assembleRelease

# Test iOS build
xcodebuild -workspace iosApp/Template.xcworkspace -scheme Template -configuration Release

# Run tests locally
./gradlew test
```

## 📋 Notes

- **Workflows are fully configurable** - update variables at the top of each file
- **Secrets must be configured** before workflows can run successfully
- **Test workflows locally** before pushing to avoid failures
- **Monitor workflow execution** to identify performance bottlenecks
- **Use appropriate runners** for different job requirements

---

**Automate your development workflow with GitHub Actions! ⚡**
