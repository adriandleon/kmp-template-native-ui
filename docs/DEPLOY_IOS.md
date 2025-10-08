# iOS Deployment Guide

This document covers the complete iOS deployment process from development to TestFlight distribution using Fastlane automation.

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [✅ Prerequisites](#-prerequisites)
- [⚙️ Environment Setup](#-environment-setup)
- [🏪 App Store Connect Setup](#-app-store-connect-setup)
- [🔐 Code Signing](#-code-signing)
- [🚀 Release Process](#-release-process)
- [⚡ Fastlane Automation](#-fastlane-automation)
- [🐛 Troubleshooting](#-troubleshooting)
- [📚 Best Practices](#-best-practices)

## 🎯 Overview

The iOS deployment process uses **Fastlane** to automate the release workflow, ensuring consistent and reliable deployments to TestFlight. This guide covers the complete setup from initial configuration to automated releases.

## ✅ Prerequisites

Before starting the deployment process, ensure you have:

- **Xcode 16.0+** installed on your Mac
- **Fastlane** installed (`gem install fastlane` or `brew install fastlane`)
- **Access to Apple Developer account** with appropriate permissions
- **Access to certificates repository** for code signing
- **Latest version of the code** checked out from repository

## 🚀 Environment Setup

### **1. Navigate to iOS App Directory**
```bash
cd iosApp
```

### **2. Install Dependencies**
```bash
# Install Ruby gems (if using bundler)
bundle install

# Or install Fastlane globally
brew install fastlane
```

### **3. Verify Fastlane Installation**
```bash
fastlane --version
```

## 🔑 App Store Connect Setup

### **App Store Connect API Key Authentication**

To authenticate with App Store Connect, we use an API Key instead of username/password authentication. This is more secure and works better with CI/CD systems.

#### **Creating an App Store Connect API Key**

1. **Log in to [App Store Connect](https://appstoreconnect.apple.com)**
2. **Navigate to Users and Access > Keys**
3. **Click the "+" button** to create a new key
4. **Give the key a name** (e.g., "Fastlane CI")
5. **Select the following access levels**:
   - App Manager
   - Developer
6. **Click "Generate"**
7. **Download the API Key file** (named `AuthKey_XXXXXXXXXX.p8`)
8. **Save the Key ID and Issuer ID** shown on the screen

#### **Setting Up the API Key in the Project**

**Option 1: Convert .p8 file content to base64**
```bash
# On macOS/Linux
base64 -i AuthKey_XXXXXXXXXX.p8 | pbcopy
```

**Option 2: Create environment file**
1. **Create a `.env` file** in the `iosApp/fastlane` directory if it doesn't exist
2. **Add the following variables** to the `.env` file:

```bash
APP_STORE_CONNECT_API_KEY_ID=XXXXXXXXXX
APP_STORE_CONNECT_ISSUER_ID=XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
APP_STORE_CONNECT_API_KEY_CONTENT=<base64-encoded-content>
APP_STORE_CONNECT_API_KEY_IS_KEY_CONTENT_BASE64=true
```

**Replace the values with your actual:**
- **Key ID**: From the App Store Connect screen
- **Issuer ID**: From the App Store Connect screen
- **Base64-encoded content**: From the base64 command output

#### **Verifying the API Key Setup**

To verify the API Key is working correctly:

```bash
fastlane ios release
```

**If authentication fails, check:**
1. **API Key file permissions** and encoding
2. **Base64 encoding** accuracy
3. **Values in the `.env` file** correctness
4. **API Key's access permissions** in App Store Connect

## 🔐 Code Signing

### **Fastlane Match Setup**

We use **fastlane match** to manage certificates and provisioning profiles. This ensures consistent code signing across the team and CI/CD systems.

#### **Initial Setup**

1. **Ensure access to certificates repository**:
   ```
   https://github.com/japsystem/apple-certificates.git
   ```

2. **If setting up a new machine or need to reset certificates**:
   ```bash
   fastlane ios sync_all_development
   ```

   This will:
   - Register all devices from the Devicefile
   - Download and install all necessary certificates and provisioning profiles
   - Set up your local environment for development

#### **Adding New Devices**

1. **Add the new device information** to `iosApp/fastlane/Devicefile`
2. **Run the sync command**:
   ```bash
   fastlane ios sync_all_development
   ```

#### **Common Match Commands**

**Sync certificates and profiles:**
```bash
# Development environment
fastlane match development

# App Store distribution
fastlane match appstore

# Ad-hoc distribution
fastlane match adhoc
```

**Force reset certificates** (use with caution):
```bash
fastlane match development --force
fastlane match appstore --force
fastlane match adhoc --force
```

**Revoke all certificates and profiles** for a specific environment (use with caution):
```bash
fastlane match nuke development
fastlane match nuke appstore
fastlane match nuke adhoc
```

#### **Troubleshooting Match Issues**

If you encounter issues with certificates:

1. **Check your access** to the certificates repository
2. **Verify your Apple Developer account** permissions
3. **Try cleaning the match repository**:
   ```bash
   fastlane match nuke development
   fastlane match nuke appstore
   ```
4. **Re-run the sync command**:
   ```bash
   fastlane ios sync_all_development
   ```

## 🚀 Release Process

### **1. Update Version and Build Number**

The build number is automatically incremented during the release process. The version number should be updated in the Xcode project settings if needed.

**Manual version update in Xcode:**
1. Open `iosApp/KMP-Template.xcodeproj`
2. Select the KMP-Template target
3. Go to General tab
4. Update Version and Build numbers as needed

### **2. Update Changelog**

Before releasing, ensure the changelog is up to date. The changelog is read from:
```
composeApp/release/whatsNew/whatsnew-es-419
```

**Changelog format:**
- Keep entries concise and clear
- Use bullet points for multiple changes
- Focus on user-facing improvements
- Include bug fixes and new features

### **3. Run Tests and Linting**

It's recommended to run the following checks before releasing:

```bash
# Run iOS tests
fastlane ios tests

# Run Swift linting
fastlane ios lint

# Auto-correct linting issues
fastlane ios lint_autocorrect
```

### **4. Release to TestFlight**

To release to TestFlight, run:

```bash
fastlane ios release
```

**This command will:**
1. **Increment the build number** automatically
2. **Sync code signing certificates** using match
3. **Build the iOS app** with proper signing
4. **Upload to TestFlight** with the changelog
5. **Provide build information** for distribution

### **5. Monitor TestFlight Build**

After uploading:

1. **Log in to [App Store Connect](https://appstoreconnect.apple.com)**
2. **Navigate to the KMP-Template app**
3. **Go to TestFlight tab**
4. **Wait for the build to process** (usually takes 15-30 minutes)
5. **Once processed, distribute the build** to testers

## ⚡ Fastlane Automation

### **Available Fastlane Lanes**

#### **Core Lanes**
```bash
# Main release lane
fastlane ios release

# Development sync
fastlane ios sync_all_development

# Testing
fastlane ios tests

# Linting
fastlane ios lint
fastlane ios lint_autocorrect

# Cleaning
fastlane ios clean
```

#### **Device Management**
```bash
# Sync device information
fastlane ios sync_device_info

# Register new devices
fastlane ios register_devices
```

#### **Certificate Management**
```bash
# Sync development certificates
fastlane ios sync_development

# Sync app store certificates
fastlane ios sync_appstore

# Force certificate refresh
fastlane ios sync_development --force
```

### **Fastfile Configuration**

The main Fastfile is located at `iosApp/fastlane/Fastfile` and contains:

- **Release lane** for TestFlight deployment
- **Development sync** for team setup
- **Testing and linting** automation
- **Certificate management** with match

### **Environment Variables**

Key environment variables used by Fastlane:

```bash
# App Store Connect API
APP_STORE_CONNECT_API_KEY_ID
APP_STORE_CONNECT_ISSUER_ID
APP_STORE_CONNECT_API_KEY_CONTENT

# Match configuration
MATCH_GIT_URL
MATCH_PASSWORD
MATCH_APP_IDENTIFIER

# Build configuration
XCODE_PROJECT
XCODE_SCHEME
XCODE_WORKSPACE
```

## 🐛 Troubleshooting

### **Common Issues**

#### **Code Signing Issues**

**Problem**: Build fails with code signing errors
**Solution**:
```bash
# Sync certificates and devices
fastlane ios sync_all_development

# Clean build folder
fastlane ios clean

# Force certificate refresh
fastlane ios sync_development --force
```

#### **Build Issues**

**Problem**: Build fails during compilation
**Solution**:
```bash
# Clean project
fastlane ios clean

# Check Xcode logs for specific errors
# Ensure all dependencies are properly installed
# Try rebuilding
fastlane ios release
```

#### **Authentication Issues**

**Problem**: App Store Connect authentication fails
**Solution**:
1. **Verify API Key permissions** in App Store Connect
2. **Check environment variables** in `.env` file
3. **Verify base64 encoding** of API key content
4. **Test API key** with simple command first

#### **Certificate Expiry**

**Problem**: Certificates are expired or invalid
**Solution**:
```bash
# Revoke and regenerate certificates
fastlane match nuke development
fastlane match nuke appstore

# Re-sync certificates
fastlane ios sync_all_development
```

### **Debug Mode**

#### **Enable Verbose Logging**
```bash
# Run with debug information
fastlane ios release --verbose

# Check specific lane
fastlane ios sync_development --verbose
```

#### **Check Configuration**
```bash
# Verify Fastlane setup
fastlane lanes

# Check environment
fastlane env
```

## 📚 Best Practices

### **1. Release Management**
- **Test thoroughly** before releasing to TestFlight
- **Use semantic versioning** for version numbers
- **Keep changelog updated** and user-friendly
- **Monitor build processing** in App Store Connect

### **2. Code Signing**
- **Use fastlane match** for consistent signing across team
- **Store certificates securely** in private repository
- **Regularly sync certificates** to avoid expiry issues
- **Test signing** on multiple devices

### **3. Automation**
- **Automate repetitive tasks** with Fastlane lanes
- **Use CI/CD integration** for automated releases
- **Maintain Fastfile** with clear lane descriptions
- **Version control** your Fastlane configuration

### **4. Testing**
- **Run tests locally** before releasing
- **Use multiple device types** for testing
- **Test on different iOS versions** when possible
- **Validate app functionality** after build

### **5. Monitoring**
- **Track build success rates** over time
- **Monitor TestFlight feedback** from testers
- **Review crash reports** and analytics
- **Maintain deployment logs** for troubleshooting

## 🔗 Related Documentation

- [GitHub Actions Workflows](GITHUB_ACTIONS.md) - CI/CD automation
- [Firebase Integration](FIREBASE_INTEGRATION.md) - Analytics and crash reporting
- [Swift Format & Lint](SWIFT_FORMAT_LINT.md) - Code quality tools
- [Code Coverage Reports](CODE_COVERAGE_REPORTS.md) - Testing coverage

## 📖 Resources

- [Fastlane Documentation](https://docs.fastlane.tools/)
- [App Store Connect API](https://developer.apple.com/documentation/appstoreconnectapi)
- [Apple Developer Documentation](https://developer.apple.com/documentation/)
- [TestFlight Guide](https://developer.apple.com/testflight/)

## 📝 Additional Commands

### **Useful Fastlane Commands**
```bash
# Sync devices
fastlane ios sync_device_info

# Run linting
fastlane ios lint

# Auto-correct linting
fastlane ios lint_autocorrect

# Clean build
fastlane ios clean

# Check available lanes
fastlane lanes
```

### **Manual Xcode Commands**
```bash
# Build for device
xcodebuild -project KMP-Template.xcodeproj -scheme KMP-Template -destination 'generic/platform=iOS' build

# Archive for distribution
xcodebuild -project KMP-Template.xcodeproj -scheme KMP-Template -destination 'generic/platform=iOS' archive -archivePath KMP-Template.xcarchive
```

## 📋 Notes

- **Release process uses fastlane match** for code signing
- **Certificates are stored** in a private repository
- **App identifier**: `com.adriandeleon.kmp.template.KMPTemplate`
- **Apple Developer account**: `invjapsystem@gmail.com`
- **Build numbers are automatically incremented** during release
- **Changelog is read from** the release notes directory

---

**Deploy iOS apps reliably with Fastlane automation! 🚀**