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

### 3. Package Name Updates

The script replaces all occurrences of:
- `com.adriandeleon.template` → `com.yourcompany.yourapp`
- `Template` → `MyAwesomeApp`
- `adriandeleon` → `yourcompany`

### 4. Bundle Identifier Updates

Updates iOS bundle identifiers:
- `com.adriandeleon.template.Template` → `com.yourcompany.yourapp.MyAwesomeApp`

### 5. Directory Cleanup

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
- **Auto-generated**: Automatically created from package name + project name
- **Format**: `packageName.projectName` (e.g., `com.yourcompany.yourapp.MyAwesomeApp`)
- **Customizable**: Option to override with custom bundle identifier if needed
- **Validation**: Must follow iOS bundle identifier conventions

## 📊 Example Transformation

### Input
```
Package Name: com.acme.awesomeapp
Project Name: AwesomeApp
Bundle ID: com.acme.awesomeapp.AwesomeApp (auto-generated)
```

### Output
```
Directory Structure:
├── shared/src/commonMain/kotlin/com/acme/awesomeapp/
├── shared/src/androidMain/kotlin/com/acme/awesomeapp/
├── shared/src/iosMain/kotlin/com/acme/awesomeapp/
├── shared/src/commonTest/kotlin/com/acme/awesomeapp/
├── composeApp/src/androidMain/kotlin/com/acme/awesomeapp/
├── iosApp/AwesomeApp/
└── iosApp/AwesomeApp.xcodeproj/

Updated Files:
- All .kt files now use package com.acme.awesomeapp
- All .swift files now reference AwesomeApp
- All build files use com.acme.awesomeapp
- All documentation references AwesomeApp
- iOS bundle identifier: com.acme.awesomeapp.AwesomeApp
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
- **Automatic**: Bundle identifier is auto-generated from package name + project name
- **Consistent**: Follows iOS naming conventions automatically
- **Flexible**: Option to customize if needed for special cases
- **Reduced Input**: No need to manually type the same identifier twice

### Complete Source Set Coverage
- **All Source Sets**: Handles `commonMain`, `androidMain`, `iosMain`, and `commonTest`
- **No Missing Files**: Ensures all Kotlin files are properly migrated
- **Consistent Structure**: Maintains the same package structure across all source sets
- **Complete Cleanup**: Removes old directories from all source sets

### Example Flow
```
Enter your package name: com.example.myapp
Enter your project name: MyApp
Generated bundle identifier: com.example.myapp.MyApp
Do you want to customize the bundle identifier? (y/N): N
```

## 📝 Post-Setup Instructions

After running the script, you'll need to:

### 1. Update Your IDE
- Close and reopen Android Studio
- Sync Gradle files
- Open the new Xcode project: `iosApp/YourApp.xcodeproj`

### 2. Update Configuration Files
- Update `google-services.json` with your Firebase project
- Update `GoogleService-Info.plist` with your Firebase project
- Update `local.properties` with your API keys

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
