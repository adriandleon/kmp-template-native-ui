# Pull Request Danger Checks

Every time a Pull Request is opened, the metadata of the pull request is checked to ensure that it
meets the minimum standards to be merged.

[Danger Kotlin](https://danger.systems/kotlin/) runs on the CI environment every time a PR is pushed
and provides automated code review assistance by codifying your team's norms and leaving helpful messages
inside your PRs based on configurable rules written in Kotlin.

In the workflow file [.github/workflows/shared_test_lint.yml](.github/workflows/shared_test_lint.yml)
there is a job `danger` with a step named `Danger Checks` that runs danger-kotlin on every Pull Request and prints its output in the PR itself.

> **Note**: This workflow is fully configurable with variables at the top. See [GitHub Actions Workflows](GITHUB_ACTIONS.md) for customization details.

## 📋 Table of Contents

- [🚀 Quick Setup](#-quick-setup)
- [🔧 Configuration Options](#-configuration-options)
- [🎯 Danger Checks](#-danger-checks)
- [🎨 Customization Examples](#-customization-examples)
- [🔗 Related Documentation](#-related-documentation)
- [📚 Best Practices](#-best-practices)

## 🚀 Quick Setup

### **Step 1: Copy Dangerfile**
Copy the `config/Dangerfile.df.kts` file to your new project.

### **Step 2: Update Configuration Variables**
Update the configuration variables at the top of the Dangerfile:

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

### **Step 3: Customize Rules**
Enable/disable specific checks based on your team's needs:

```kotlin
// PR Quality Configuration
val ENABLE_PR_SIZE_WARNING = true                       // Enable PR size warnings
val ENABLE_LABEL_CHECKS = true                          // Enable label requirement checks
val ENABLE_DESCRIPTION_CHECK = true                     // Enable PR description requirement
val RELEASE_NOTES_ENABLED = false                       // Disable if not using release notes
```

## 🔧 Configuration Options

### **Project Configuration**
```kotlin
val PROJECT_NAME = "KMP-Template"                      // Your project name
val PROJECT_OWNER = "adriandleon"                       // GitHub username/organization
val PROJECT_REPO = "kmp-template-native-ui"             // Repository name
```

### **Module Paths**
```kotlin
val SHARED_MODULE_PATH = "shared/src/commonMain/"       // Path to shared module common code
val ANDROID_MODULE_PATH = "composeApp/src/androidMain/" // Path to Android module code
val IOS_MODULE_PATH = "iosApp/KMP-Template/"            // Path to iOS module code
```

### **Release Notes Configuration**
```kotlin
val RELEASE_NOTES_ENABLED = true                        // Enable/disable release notes checks
val RELEASE_NOTES_LANGUAGES = mapOf(
    "en-US" to "whatsnew-en-US",                        // English (US)
    "es-419" to "whatsnew-es-419",                      // Spanish (Latin America)
    "pt-BR" to "whatsnew-pt-BR"                         // Portuguese (Brazil)
)
```

### **PR Quality Configuration**
```kotlin
val MAX_PR_SIZE = 700                                   // Maximum PR size before warning
val ENABLE_PR_SIZE_WARNING = true                       // Enable/disable PR size warnings
val ENABLE_LABEL_CHECKS = true                          // Enable/disable label requirement checks
val ENABLE_DESCRIPTION_CHECK = true                     // Enable/disable PR description requirement
```

### **Label Configuration**
```kotlin
val REQUIRED_LABELS = listOf("android", "ios", "shared") // Labels to check for
val WIP_LABELS = listOf("wip", "work-in-progress", "draft") // Labels that indicate WIP
```

## Grant permissions for Danger

Grant `read and write` permissions to the GITHUB_TOKEN for workflows in GitHub:

- Go to `Settings -> Code and automation -> Actions -> General` in your repository
- In the `Workflow permissions` section, select `Read and write permissions` and save.

## 🎯 Danger Checks

These are the automated checks that Danger Kotlin runs on every pull request:

### **Thank the author of the PR**
Automatically thanks the PR author with a personalized message:

```kotlin
message("Thanks: @${pullRequest.user.login}! 🎉")
```

### **PR Description Check**
Ensures Pull Requests have meaningful descriptions:

```kotlin
if (ENABLE_DESCRIPTION_CHECK && pullRequest.body.isNullOrBlank()) {
    fail("📝 Please provide a summary in the Pull Request description.")
}
```

### **PR Size Warning**
Warns when PRs become too large (configurable threshold):

```kotlin
if (ENABLE_PR_SIZE_WARNING && (pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0) > MAX_PR_SIZE) {
    warn("✂️ Please consider breaking up this pull request. Current size: ${(pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0)} lines")
}
```

### **Label Requirements**
Suggests appropriate labels based on modified files:

```kotlin
// Suggest Android label when Android files are modified
if (androidModified && !hasAndroidLabel) {
    warn("🏷️ Please add an `android` label to this pull request.")
}

// Suggest iOS label when iOS files are modified
if (iosModified && !hasIosLabel) {
    warn("🏷️ Please add an `ios` label to this pull request.")
}

// Suggest shared label when shared files are modified
if (sharedModified && !hasSharedLabel) {
    warn("🏷️ Please add a `shared` label to this pull request.")
}
```

### **Code Cleanup Detection**
Celebrates when PRs remove more code than they add:

```kotlin
if ((pullRequest.deletions ?: 0) > (pullRequest.additions ?: 0)) {
    message("🎉 Code Cleanup! Great job removing unused code!")
}
```

### **Work in Progress Check**
Identifies PRs marked as work in progress:

```kotlin
if (pullRequest.title.contains("WIP", false) || hasWipLabel) {
    warn("⌛️ PR is classed as Work in Progress")
}
```

### **Release Notes Validation**
Ensures release notes are updated when Android code changes (if enabled):

```kotlin
if (RELEASE_NOTES_ENABLED && androidModified) {
    RELEASE_NOTES_LANGUAGES.forEach { (locale, filename) ->
        val whatsNewFile = "$ANDROID_RELEASE_NOTES_DIR/$filename"
        val whatsNewModified = allSourceFiles.contains(whatsNewFile)
        
        if (!whatsNewModified) {
            val languageName = when (locale) {
                "en-US" -> "English 🇺🇸"
                "es-419" -> "Spanish 🇪🇸"
                "pt-BR" -> "Portuguese 🇧🇷"
                else -> locale
            }
            
            message("🚀 Please add what's new information for release in $languageName.\nYou can find it at [$filename](${getReleaseNotesUrl(filename)})")
        }
    }
}
```

### **Module Summary**
Provides a summary of which modules were modified:

```kotlin
val modifiedModules = mutableListOf<String>().apply {
    if (sharedModified) add("shared")
    if (androidModified) add("Android")
    if (iosModified) add("iOS")
}

if (modifiedModules.isNotEmpty()) {
    message("📱 This PR modifies: ${modifiedModules.joinToString(", ")}")
}
```

## 🎨 Customization Examples

### **Add New Language Support**
```kotlin
val RELEASE_NOTES_LANGUAGES = mapOf(
    "en-US" to "whatsnew-en-US",                        // English (US)
    "es-419" to "whatsnew-es-419",                      // Spanish (Latin America)
    "pt-BR" to "whatsnew-pt-BR",                        // Portuguese (Brazil)
    "fr-FR" to "whatsnew-fr-FR",                        // French (France)
    "de-DE" to "whatsnew-de-DE"                         // German (Germany)
)
```

### **Custom Module Paths**
```kotlin
val SHARED_MODULE_PATH = "core/src/commonMain/"          // Different shared module path
val ANDROID_MODULE_PATH = "mobile/src/main/"             // Different Android module path
val IOS_MODULE_PATH = "ios/MyApp/"                       // Different iOS module path
```

### **Disable Specific Checks**
```kotlin
val ENABLE_PR_SIZE_WARNING = false                       // Disable PR size warnings
val ENABLE_LABEL_CHECKS = false                          // Disable label requirement checks
val RELEASE_NOTES_ENABLED = false                        // Disable release notes checks
```

### **Custom Label Requirements**
```kotlin
val REQUIRED_LABELS = listOf("feature", "bugfix", "docs", "refactor") // Custom label set
val WIP_LABELS = listOf("draft", "in-progress", "review-needed")      // Custom WIP labels
```

## 🔗 Related Documentation

- [GitHub Actions Workflows](GITHUB_ACTIONS.md) - CI/CD workflow configuration
- [Danger Kotlin Documentation](https://danger.systems/kotlin/) - Official Danger Kotlin docs
- [Danger Systems](https://danger.systems/) - Main Danger documentation

## 📚 Best Practices

1. **Keep configuration at the top** for easy maintenance
2. **Use descriptive variable names** that clearly indicate their purpose
3. **Group related variables** with clear section headers
4. **Document any project-specific requirements** in comments
5. **Test rules** in a fork before applying to main project
6. **Version control Dangerfile** alongside your project code
7. **Regularly review and update** rules based on team feedback

---

**Happy automating your code reviews! 🚀**