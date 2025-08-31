import systems.danger.kotlin.*

// =============================================================================
// DANGER CONFIGURATION - Update these variables for your project
// =============================================================================

// Project Configuration
val PROJECT_NAME = "Template"                                    // Your project name
val PROJECT_OWNER = "adriandleon"                                // GitHub username/organization
val PROJECT_REPO = "kmp-template-native-ui"                      // Repository name

// Module Paths - Update these to match your project structure
val SHARED_MODULE_PATH = "shared/src/commonMain/"                // Path to shared module common code
val ANDROID_MODULE_PATH = "composeApp/src/androidMain/"          // Path to Android module code
val IOS_MODULE_PATH = "iosApp/Template/"                         // Path to iOS module code

// Android App Configuration
val ANDROID_APP_MODULE = "composeApp"                            // Your Android module name
val ANDROID_RELEASE_NOTES_DIR = "composeApp/release/whatsNew"    // Path to release notes directory

// Release Notes Configuration
val RELEASE_NOTES_ENABLED = true                                 // Enable/disable release notes checks
val RELEASE_NOTES_LANGUAGES = mapOf(
    "en-US" to "whatsnew-en-US",                                // English (US)
    "es-419" to "whatsnew-es-419",                              // Spanish (Latin America)
    "pt-BR" to "whatsnew-pt-BR"                                 // Portuguese (Brazil)
)

// PR Quality Configuration
val MAX_PR_SIZE = 700                                            // Maximum PR size before warning
val ENABLE_PR_SIZE_WARNING = true                                // Enable/disable PR size warnings
val ENABLE_LABEL_CHECKS = true                                   // Enable/disable label requirement checks
val ENABLE_DESCRIPTION_CHECK = true                              // Enable/disable PR description requirement

// Label Configuration
val REQUIRED_LABELS = listOf("android", "ios", "shared")         // Labels to check for
val WIP_LABELS = listOf("wip", "work-in-progress", "draft")     // Labels that indicate WIP

// =============================================================================
// DANGER RULES - Main logic (usually no changes needed below this line)
// =============================================================================

danger(args) {

    val allSourceFiles = git.modifiedFiles + git.createdFiles

    // Check which modules were modified
    val sharedModified = allSourceFiles.any { it.contains(SHARED_MODULE_PATH) }
    val androidModified = allSourceFiles.any { it.contains(ANDROID_MODULE_PATH) }
    val iosModified = allSourceFiles.any { it.contains(IOS_MODULE_PATH) }

    // Check release notes files if enabled
    val releaseNotesModified = if (RELEASE_NOTES_ENABLED) {
        RELEASE_NOTES_LANGUAGES.values.any { language ->
            allSourceFiles.contains("$ANDROID_RELEASE_NOTES_DIR/$language")
        }
    } else {
        false
    }

    onGitHub {

        val hasWipLabel = issue.labels.any { label ->
            WIP_LABELS.any { wipLabel -> 
                label.name.contains(wipLabel, ignoreCase = true) 
            }
        }
        
        val hasAndroidLabel = issue.labels.any { it.name.contains("android", ignoreCase = true) }
        val hasIosLabel = issue.labels.any { it.name.contains("ios", ignoreCase = true) }
        val hasSharedLabel = issue.labels.any { it.name.contains("shared", ignoreCase = true) }

        // Thank the author of the Pull Request
        message("Thanks: @${pullRequest.user.login}! 🎉")

        // Check if Pull Request has description (if enabled)
        if (ENABLE_DESCRIPTION_CHECK && pullRequest.body.isNullOrBlank()) {
            fail("📝 Please provide a summary in the Pull Request description.")
        }

        // Warn if PR size is too large (if enabled)
        if (ENABLE_PR_SIZE_WARNING && (pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0) > MAX_PR_SIZE) {
            warn("✂️ Please consider breaking up this pull request. Current size: ${(pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0)} lines")
        }

        // Label checks (if enabled)
        if (ENABLE_LABEL_CHECKS) {
            // Warn if Pull Request has no labels
            if (issue.labels.isEmpty()) {
                warn("🏷️ Please add labels to this pull request.")
            }

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
        }

        // Detect if pull request consists of code cleanup
        if ((pullRequest.deletions ?: 0) > (pullRequest.additions ?: 0)) {
            message("🎉 Code Cleanup! Great job removing unused code!")
        }

        // Work in progress check
        if (pullRequest.title.contains("WIP", false) || hasWipLabel) {
            warn("⌛️ PR is classed as Work in Progress")
        }

        // Release notes checks (if enabled)
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

        // Summary message
        val modifiedModules = mutableListOf<String>().apply {
            if (sharedModified) add("shared")
            if (androidModified) add("Android")
            if (iosModified) add("iOS")
        }
        
        if (modifiedModules.isNotEmpty()) {
            message("📱 This PR modifies: ${modifiedModules.joinToString(", ")}")
        }
    }
}

// =============================================================================
// HELPER FUNCTIONS
// =============================================================================

/**
 * Generate the URL for release notes files
 */
fun getReleaseNotesUrl(filename: String): String {
    return "https://github.com/$PROJECT_OWNER/$PROJECT_REPO/blob/main/$ANDROID_RELEASE_NOTES_DIR/$filename"
}