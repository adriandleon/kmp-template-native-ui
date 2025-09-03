# Project Setup Script

This document describes the `setup_new_project.sh` script that transforms the Kotlin Multiplatform template into a new project with custom names, package identifiers, and folder structure.

## 🎯 Overview

The setup script automates the process of customizing the template project by:

- **Renaming project structure**: Updates all references from template names to your custom project names
- **Updating package identifiers**: Changes package names throughout the codebase
- **Restructuring directories**: Creates new directory structure based on your package name
- **Updating configuration files**: Modifies all build files, manifests, and configuration files
- **Updating documentation**: Updates all documentation files with your project information
- **Updating CI/CD workflows**: Modifies GitHub Actions workflows and other automation scripts

## 🚀 Usage

### Prerequisites

- **Bash shell** (macOS, Linux, or WSL on Windows)
- **Git repository** cloned from the template
- **Write permissions** to the project directory

### Running the Script

1. **Navigate to the project root directory**:
   ```bash
   cd /path/to/your/template/project
   ```

2. **Make the script executable** (if not already):
   ```bash
   chmod +x setup_new_project.sh
   ```

3. **Run the script**:
   ```bash
   ./setup_new_project.sh
   ```

4. **Follow the interactive prompts**:
   - Enter your package name (e.g., `com.yourcompany.yourapp`)
   - Enter your project name (e.g., `MyAwesomeApp`)
   - Review the auto-generated bundle identifier (e.g., `com.yourcompany.yourapp.MyAwesomeApp`)
   - Optionally customize the bundle identifier if needed
   - Confirm the transformation

## 📋 What the Script Does

### 1. Directory Structure Transformation

**Before** (Template):
```
shared/src/commonMain/kotlin/com/adriandeleon/template/
shared/src/androidMain/kotlin/com/adriandeleon/template/
shared/src/iosMain/kotlin/com/adriandeleon/template/
shared/src/commonTest/kotlin/com/adriandeleon/template/
composeApp/src/androidMain/kotlin/com/adriandeleon/template/
iosApp/Template/
iosApp/Template.xcodeproj/
```

**After** (Your Project):
```
shared/src/commonMain/kotlin/com/yourcompany/yourapp/
shared/src/androidMain/kotlin/com/yourcompany/yourapp/
shared/src/iosMain/kotlin/com/yourcompany/yourapp/
shared/src/commonTest/kotlin/com/yourcompany/yourapp/
composeApp/src/androidMain/kotlin/com/yourcompany/yourapp/
iosApp/MyAwesomeApp/
iosApp/MyAwesomeApp.xcodeproj/
```

**Complete Coverage**: The script handles all source sets (`commonMain`, `androidMain`, `iosMain`, `commonTest`) ensuring complete package migration.

**Cleanup**: The script automatically removes all old empty directories, ensuring no leftover folder structure remains from the template.

### 2. File Content Updates

The script updates the following types of files:

#### **Configuration Files**
- `build.gradle.kts` (root and modules)
- `settings.gradle.kts`
- `gradle.properties`
- `local.properties`
- `buildServer.json`

#### **Android Files**
- `composeApp/src/androidMain/AndroidManifest.xml`
- All Kotlin source files in `composeApp/src/androidMain/kotlin/`
- All test files in `composeApp/src/androidUnitTest/kotlin/`

#### **iOS Files**
- `iosApp/Configuration/Config.xcconfig`
- `iosApp/YourApp/Info.plist`
- All Swift source files in `iosApp/YourApp/`
- Xcode project file (`project.pbxproj`)

#### **Shared Module Files**
- All Kotlin source files in `shared/src/commonMain/kotlin/`
- All test files in `shared/src/commonTest/kotlin/`

#### **Documentation Files**
- `README.md`
- All files in `docs/` directory

#### **CI/CD Files**
- GitHub Actions workflows in `.github/workflows/`
- `config/Dangerfile.df.kts`

#### **Firebase Configuration Files**
- `composeApp/google-services.json` - Android Firebase configuration with correct package name
- `iosApp/YourApp/GoogleService-Info.plist` - iOS Firebase configuration with correct bundle ID

#### **API Key Configuration Files**
- `local.properties` - API key placeholders for Supabase and ConfigCat services

### 3. Package Name Updates

The script replaces all occurrences of:
- `com.adriandeleon.template` → `com.yourcompany.yourapp`
- `Template` → `MyAwesomeApp`
- `adriandeleon` → `yourcompany`

### 4. Bundle Identifier Updates

Updates iOS bundle identifiers:
- `com.adriandeleon.template.Template` → `com.yourcompany.yourapp.MyAwesomeApp`

### 5. Firebase Configuration Creation

The script automatically creates template Firebase configuration files with the correct identifiers:
- **google-services.json**: Created with the correct Android package name
- **GoogleService-Info.plist**: Created with the correct iOS bundle identifier
- **Valid format**: Uses properly formatted API keys that meet Firebase requirements (39 characters, starts with 'A')
- **Template values**: All other values are placeholders that need to be replaced with real Firebase project data
- **Ready to use**: Files are created in the correct locations and can be replaced with actual Firebase configs

### 6. API Key Configuration Creation

The script automatically creates or updates `local.properties` with API key placeholders:
- **Supabase credentials**: Development and production URLs and keys
- **ConfigCat SDK keys**: iOS and Android live and test keys
- **Safe appending**: Adds placeholders to existing file without overwriting existing content
- **Duplicate prevention**: Checks if placeholders already exist before adding them

### 7. Directory Cleanup

The script performs comprehensive cleanup to remove all old directory structures:
- **Removes old package directories**: Deletes the entire old package structure
- **Cleans up empty directories**: Recursively removes any empty parent directories left behind
- **Prevents orphaned folders**: Ensures no leftover empty folders remain from the template

## 🔧 Input Validation

The script validates all inputs to ensure they follow proper conventions:

### Package Name Validation
- Must start with a lowercase letter
- Can contain lowercase letters, numbers, and dots
- Must follow Java package naming conventions
- Example: `com.yourcompany.yourapp`

### Project Name Validation
- Must start with a letter
- Can contain only alphanumeric characters
- Example: `MyAwesomeApp`

### Bundle Identifier Generation
- **Auto-generated**: Uses the same identifier as Android package name (standard practice)
- **Format**: `packageName` (e.g., `com.yourcompany.yourapp`)
- **Consistency**: iOS and Android use the same identifier for easier management
- **Customizable**: Option to override with custom bundle identifier if needed
- **Validation**: Must follow iOS bundle identifier conventions

## 📊 Example Transformation

### Input
```
Package Name: org.example.project
Project Name: MyApp
Bundle ID: org.example.project (auto-generated, same as package name)
Domain: project.example.org (auto-generated from package name)
```

### Output
```
Directory Structure:
├── shared/src/commonMain/kotlin/org/example/project/
├── shared/src/androidMain/kotlin/org/example/project/
├── shared/src/iosMain/kotlin/org/example/project/
├── shared/src/commonTest/kotlin/org/example/project/
├── composeApp/src/androidMain/kotlin/org/example/project/
├── iosApp/MyApp/
└── iosApp/MyApp.xcodeproj/

Updated Files:
- All .kt files now use package org.example.project
- All .swift files now reference MyApp
- All build files use org.example.project
- All documentation references MyApp
- iOS bundle identifier: org.example.project (same as Android package)
- Domain: project.example.org (reversed from package name)
- Firebase configs created with correct package names and bundle IDs
- local.properties created with API key placeholders for Supabase and ConfigCat
```

## 🛡️ Safety Features

### Backup Creation
- Creates `.backup` files for all modified files
- Automatically cleans up backup files after successful transformation

### Validation
- Validates input formats before processing
- Confirms transformation with user before proceeding
- Validates final result to ensure success

### Error Handling
- Exits on any error (`set -e`)
- Provides clear error messages
- Maintains project integrity on failure

## 🎨 Output and Feedback

The script provides colored, informative output:

- **🔵 Blue**: Current step being executed
- **🟢 Green**: Successful operations
- **🟡 Yellow**: Warnings and important information
- **🔴 Red**: Errors and failures
- **🟣 Purple**: Headers and important sections
- **🔵 Cyan**: Information and instructions

## 🚀 Improved User Experience

### Smart Bundle Identifier Generation
- **Automatic**: Bundle identifier matches the Android package name (standard practice)
- **Consistent**: iOS and Android use the same identifier for easier management
- **Flexible**: Option to customize if needed for special cases
- **Reduced Input**: No need to manually type the same identifier twice

### Complete Source Set Coverage
- **All Source Sets**: Handles `commonMain`, `androidMain`, `iosMain`, and `commonTest`
- **No Missing Files**: Ensures all Kotlin files are properly migrated
- **Consistent Structure**: Maintains the same package structure across all source sets
- **Complete Cleanup**: Removes old directories from all source sets

### Firebase Configuration Templates
- **Automatic Creation**: Creates template Firebase configuration files with correct identifiers
- **Correct Package Names**: `google-services.json` uses the exact Android package name
- **Correct Bundle IDs**: `GoogleService-Info.plist` uses the exact iOS bundle identifier
- **Valid API Keys**: Uses properly formatted API keys that meet Firebase requirements (39 characters, starts with 'A')
- **Ready for Replacement**: Template files can be directly replaced with real Firebase configs
- **No Manual Editing**: No need to manually update package names in Firebase configs

### API Key Configuration Templates
- **Automatic Creation**: Creates or updates `local.properties` with API key placeholders
- **Safe Appending**: Adds placeholders to existing file without overwriting content
- **Complete Coverage**: Includes Supabase and ConfigCat API key placeholders
- **Duplicate Prevention**: Checks if placeholders already exist before adding them
- **Ready for Configuration**: Users can replace placeholders with actual API keys

### Example Flow
```
Enter your package name: org.example.project
Enter your project name: MyApp
Generated bundle identifier: org.example.project
Note: iOS bundle identifier matches Android package name (standard practice)
Do you want to customize the bundle identifier? (y/N): N
```

## 📝 Post-Setup Instructions

After running the script, you'll need to:

### 1. Update Your IDE
- Close and reopen Android Studio
- Sync Gradle files
- Open the new Xcode project: `iosApp/YourApp.xcodeproj`

### 2. Update Configuration Files
- Replace `composeApp/google-services.json` with your actual Firebase configuration
- Replace `iosApp/YourApp/GoogleService-Info.plist` with your actual Firebase configuration
- Update `local.properties` with your actual API keys (placeholders were added)
- Note: Template files were created with correct package names and bundle IDs

### 3. Update GitHub Repository
- Update repository secrets in GitHub Settings
- Update workflow variables if needed
- Update repository name and description

### 4. Test Your Setup
```bash
# Run all tests
./gradlew test

# Test Android build
./gradlew :composeApp:assembleDebug

# Test iOS build in Xcode
```

## 🐛 Troubleshooting

### Common Issues

#### Script Permission Denied
```bash
chmod +x setup_new_project.sh
```

#### Invalid Package Name
- Ensure package name follows Java conventions
- Use lowercase letters and dots only
- Example: `com.yourcompany.yourapp`

#### Xcode Project Issues
- Make sure Xcode is closed before running the script
- Reopen Xcode after the transformation is complete

#### Gradle Sync Issues
- Clean and rebuild the project: `./gradlew clean build`
- Invalidate caches in Android Studio

#### Firebase Configuration Issues
- **Template files created**: The script creates template Firebase config files with correct package names
- **Valid API keys**: Template files use properly formatted API keys that meet Firebase requirements
- **Replace with real configs**: Download actual Firebase configuration files from Firebase Console
- **Package name must match**: Ensure the package name in Firebase Console matches your project's package name
- **Bundle ID must match**: Ensure the bundle ID in Firebase Console matches your project's bundle ID
- **File locations**: 
  - Android: `composeApp/google-services.json`
  - iOS: `iosApp/YourApp/GoogleService-Info.plist`

#### API Key Configuration Issues
- **Placeholders added**: The script automatically adds API key placeholders to `local.properties`
- **Replace placeholders**: Update the placeholder values with your actual API keys
- **Supabase keys**: Get your Supabase URL and API key from your Supabase project dashboard
- **ConfigCat keys**: Get your ConfigCat SDK keys from your ConfigCat dashboard
- **File location**: `local.properties` (in project root)

#### Empty Directories Left Behind
If you notice empty directories from the old package structure:
- The script should automatically clean these up
- If they persist, manually remove them:
  ```bash
  # Remove empty directories recursively
  find . -type d -empty -delete
  ```
- Or restore from Git and run the script again:
  ```bash
  git checkout .
  ./setup_new_project.sh
  ```

### Getting Help

If you encounter issues:

1. **Check the script output** for specific error messages
2. **Verify your inputs** follow the required formats
3. **Ensure you have write permissions** to the project directory
4. **Check that no IDEs are open** during the transformation

## 🔄 Reverting Changes

If you need to revert the transformation:

1. **Restore from Git** (recommended):
   ```bash
   git checkout .
   git clean -fd
   ```

2. **Use backup files** (if available):
   ```bash
   find . -name "*.backup" -exec sh -c 'mv "$1" "${1%.backup}"' _ {} \;
   ```

## 📚 Related Documentation

- [README.md](README.md) - Main project documentation
- [docs/DEPLOY_ANDROID.md](docs/DEPLOY_ANDROID.md) - Android deployment guide
- [docs/DEPLOY_IOS.md](docs/DEPLOY_IOS.md) - iOS deployment guide
- [docs/GITHUB_ACTIONS.md](docs/GITHUB_ACTIONS.md) - CI/CD setup guide

---

**Happy coding! 🚀**
