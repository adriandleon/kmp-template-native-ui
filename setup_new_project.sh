#!/bin/bash

# =============================================================================
# Kotlin Multiplatform KMP-Template Project Setup Script
# =============================================================================
# This script transforms the template project into a new project with custom
# names, package identifiers, and folder structure.
#
# Usage: ./setup_new_project.sh
# =============================================================================

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to print colored output
print_step() {
    echo -e "${BLUE}==>${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_info() {
    echo -e "${CYAN}ℹ${NC} $1"
}

# Function to validate input
validate_input() {
    local input="$1"
    local pattern="$2"
    local error_msg="$3"
    
    if [[ ! $input =~ $pattern ]]; then
        print_error "$error_msg"
        return 1
    fi
    return 0
}

# Function to convert string to valid package name
to_package_name() {
    echo "$1" | tr '[:upper:]' '[:lower:]' | sed 's/[^a-z0-9.]//g' | sed 's/\.\.*/\./g' | sed 's/^\.\|\.$//g'
}

# Function to convert string to valid project name
to_project_name() {
    echo "$1" | sed 's/[^a-zA-Z0-9]//g' | sed 's/^[0-9]*//g'
}

to_swift_module_name() {
    echo "$1" | sed 's/[^a-zA-Z0-9_]/_/g'
}

# Function to get domain from package name (reversed domain)
# Converts: org.example.project -> project.example.org
get_domain() {
    echo "$1" | tr '.' '\n' | awk '{lines[NR]=$0} END {for(i=NR;i>=1;i--) printf "%s%s", lines[i], (i>1 ? "." : "\n")}'
}

# Function to create directory structure
create_directory_structure() {
    local old_package="$1"
    local new_package="$2"
    local old_path=$(echo "$old_package" | tr '.' '/')
    local new_path=$(echo "$new_package" | tr '.' '/')
    
    print_step "Creating new directory structure..."
    
    # Create new directory structure for shared module - all source sets
    local shared_source_sets=("commonMain" "androidMain" "iosMain" "commonTest")
    for source_set in "${shared_source_sets[@]}"; do
        if [ -d "shared/src/$source_set/kotlin/$old_path" ]; then
            mkdir -p "shared/src/$source_set/kotlin/$new_path"
            cp -r "shared/src/$source_set/kotlin/$old_path"/* "shared/src/$source_set/kotlin/$new_path/" 2>/dev/null || true
            print_success "Created shared/$source_set directory structure: $new_path"
        fi
    done
    
    # Create new directory structure for Android source and test source sets.
    local android_source_sets=("main" "test" "androidTest")
    for source_set in "${android_source_sets[@]}"; do
        if [ -d "androidApp/src/$source_set/kotlin/$old_path" ]; then
            mkdir -p "androidApp/src/$source_set/kotlin/$new_path"
            cp -r "androidApp/src/$source_set/kotlin/$old_path"/* "androidApp/src/$source_set/kotlin/$new_path/" 2>/dev/null || true
            print_success "Created androidApp/$source_set directory structure: $new_path"
        fi
    done

    if [ -d "konsistTest/src/test/kotlin/$old_path" ]; then
        mkdir -p "konsistTest/src/test/kotlin/$new_path"
        cp -r "konsistTest/src/test/kotlin/$old_path"/* "konsistTest/src/test/kotlin/$new_path/" 2>/dev/null || true
        print_success "Created konsistTest directory structure: $new_path"
    fi

    if [ -d "shared/schemas/$old_package.db.AppDatabase" ]; then
        mkdir -p "shared/schemas/$new_package.db.AppDatabase"
        cp -r "shared/schemas/$old_package.db.AppDatabase"/* "shared/schemas/$new_package.db.AppDatabase/" 2>/dev/null || true
        print_success "Created Room schema directory: $new_package.db.AppDatabase"
    fi
}

# Function to remove old directory structure
remove_old_directories() {
    local old_package="$1"
    local old_path=$(echo "$old_package" | tr '.' '/')
    
    print_step "Removing old directory structure..."
    
    # Remove old directories from shared module - all source sets
    local shared_source_sets=("commonMain" "androidMain" "iosMain" "commonTest")
    for source_set in "${shared_source_sets[@]}"; do
        rm -rf "shared/src/$source_set/kotlin/$old_path" 2>/dev/null || true
    done
    
    # Remove old directories from Android source and test source sets.
    local android_source_sets=("main" "test" "androidTest")
    for source_set in "${android_source_sets[@]}"; do
        rm -rf "androidApp/src/$source_set/kotlin/$old_path" 2>/dev/null || true
    done

    rm -rf "konsistTest/src/test/kotlin/$old_path" 2>/dev/null || true
    rm -rf "shared/schemas/$old_package.db.AppDatabase" 2>/dev/null || true
    
    # Clean up empty parent directories
    for source_set in "${shared_source_sets[@]}"; do
        cleanup_empty_directories "shared/src/$source_set/kotlin"
    done
    for source_set in "${android_source_sets[@]}"; do
        cleanup_empty_directories "androidApp/src/$source_set/kotlin"
    done
    cleanup_empty_directories "konsistTest/src/test/kotlin"
    cleanup_empty_directories "shared/schemas"
    
    print_success "Removed old directory structure: $old_path"
}

# Function to clean up empty directories recursively
cleanup_empty_directories() {
    local base_path="$1"
    
    if [ -d "$base_path" ]; then
        # Find and remove empty directories, starting from the deepest level
        find "$base_path" -type d -empty -delete 2>/dev/null || true
        
        # Also check for directories that only contain empty subdirectories
        # This handles cases where intermediate directories might be left empty
        while true; do
            local empty_dirs=$(find "$base_path" -type d -empty 2>/dev/null | wc -l)
            if [ "$empty_dirs" -eq 0 ]; then
                break
            fi
            find "$base_path" -type d -empty -delete 2>/dev/null || true
        done
    fi
}

# Function to update file contents
update_file_contents() {
    local file="$1"
    local old_package="$2"
    local new_package="$3"
    local old_project_name="$4"
    local new_project_name="$5"
    local old_bundle_id="$6"
    local new_bundle_id="$7"
    local old_domain="$8"
    local new_domain="$9"
    local old_swift_module
    local new_swift_module

    old_swift_module=$(to_swift_module_name "$old_project_name")
    new_swift_module=$(to_swift_module_name "$new_project_name")
    
    if [ -f "$file" ]; then
        # Create backup
        cp "$file" "$file.backup"

        # Replace full bundle identifiers before package names so app target
        # bundle IDs do not get stuck with the old project suffix.
        sed -i.tmp "s|$old_bundle_id|$new_bundle_id.$new_project_name|g" "$file"
        
        # Replace package names
        sed -i.tmp "s|$old_package|$new_package|g" "$file"
        
        # Replace project names
        sed -i.tmp "s|$old_project_name|$new_project_name|g" "$file"

        # Replace Swift module names and display names that differ from the
        # filesystem-safe Xcode project name.
        sed -i.tmp "s|$old_swift_module|$new_swift_module|g" "$file"
        sed -i.tmp "s|KMP Template|$new_project_name|g" "$file"
        
        # Replace domain names
        sed -i.tmp "s|$old_domain|$new_domain|g" "$file"
        
        # Clean up temporary files
        rm -f "$file.tmp"
        
        print_success "Updated: $file"
    fi
}

# Function to update Xcode project
update_xcode_project() {
    local old_package="$1"
    local new_package="$2"
    local old_project_name="$3"
    local new_project_name="$4"
    local old_bundle_id="$5"
    local new_bundle_id="$6"
    
    print_step "Updating Xcode project..."
    
    # Update project.pbxproj
    if [ -f "iosApp/$old_project_name.xcodeproj/project.pbxproj" ]; then
        update_file_contents "iosApp/$old_project_name.xcodeproj/project.pbxproj" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "adriandeleon" "$(echo $new_bundle_id | cut -d'.' -f1)"
        
        # Rename Xcode project directory
        mv "iosApp/$old_project_name.xcodeproj" "iosApp/$new_project_name.xcodeproj"
        print_success "Renamed Xcode project directory"
    fi
    
    # Update Config.xcconfig
    if [ -f "iosApp/Configuration/Config.xcconfig" ]; then
        update_file_contents "iosApp/Configuration/Config.xcconfig" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "adriandeleon" "$(echo $new_bundle_id | cut -d'.' -f1)"
    fi
    
    # Rename iOS app folder
    if [ -d "iosApp/$old_project_name" ]; then
        mv "iosApp/$old_project_name" "iosApp/$new_project_name"
        print_success "Renamed iOS app folder"
    fi

    # Rename the Swift test target folder so the synchronized Xcode group path
    # keeps matching the filesystem after project renaming.
    if [ -d "iosApp/${old_project_name}Tests" ]; then
        mv "iosApp/${old_project_name}Tests" "iosApp/${new_project_name}Tests"
        print_success "Renamed iOS test folder"
    fi
    
    # Update Swift files in the new folder
    find "iosApp/$new_project_name" -name "*.swift" -type f | while read -r file; do
        update_file_contents "$file" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "adriandeleon" "$(echo $new_bundle_id | cut -d'.' -f1)"
    done

    # Update Swift test files in the renamed test target folder.
    find "iosApp/${new_project_name}Tests" -name "*.swift" -type f | while read -r file; do
        update_file_contents "$file" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "adriandeleon" "$(echo $new_bundle_id | cut -d'.' -f1)"
    done

    if [ -f "iosApp/${new_project_name}Tests/KMP_TemplateTests.swift" ]; then
        mv "iosApp/${new_project_name}Tests/KMP_TemplateTests.swift" "iosApp/${new_project_name}Tests/${new_project_name}Tests.swift"
        print_success "Renamed iOS root test file"
    elif [ -f "iosApp/${new_project_name}Tests/${old_project_name}Tests.swift" ]; then
        mv "iosApp/${new_project_name}Tests/${old_project_name}Tests.swift" "iosApp/${new_project_name}Tests/${new_project_name}Tests.swift"
        print_success "Renamed iOS root test file"
    fi
}

# Function to update all source files
update_source_files() {
    local old_package="$1"
    local new_package="$2"
    local old_project_name="$3"
    local new_project_name="$4"
    local old_bundle_id="$5"
    local new_bundle_id="$6"
    local old_domain="$7"
    local new_domain="$8"
    
    print_step "Updating source files..."
    
    # Update Kotlin files
    find . -name "*.kt" -type f | while read -r file; do
        update_file_contents "$file" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    done
    
    # Update Swift files
    find . -name "*.swift" -type f | while read -r file; do
        update_file_contents "$file" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    done
    
    print_success "Updated all source files"
}

# Function to update configuration files
update_config_files() {
    local old_package="$1"
    local new_package="$2"
    local old_project_name="$3"
    local new_project_name="$4"
    local old_bundle_id="$5"
    local new_bundle_id="$6"
    local old_domain="$7"
    local new_domain="$8"
    
    print_step "Updating configuration files..."
    
    # Update Gradle files
    update_file_contents "build.gradle.kts" \
        "$old_package" "$new_package" \
        "$old_project_name" "$new_project_name" \
        "$old_bundle_id" "$new_bundle_id" \
        "$old_domain" "$new_domain"
    
    update_file_contents "settings.gradle.kts" \
        "$old_package" "$new_package" \
        "$old_project_name" "$new_project_name" \
        "$old_bundle_id" "$new_bundle_id" \
        "$old_domain" "$new_domain"
    
    update_file_contents "androidApp/build.gradle.kts" \
        "$old_package" "$new_package" \
        "$old_project_name" "$new_project_name" \
        "$old_bundle_id" "$new_bundle_id" \
        "$old_domain" "$new_domain"
    
    update_file_contents "shared/build.gradle.kts" \
        "$old_package" "$new_package" \
        "$old_project_name" "$new_project_name" \
        "$old_bundle_id" "$new_bundle_id" \
        "$old_domain" "$new_domain"

    find "shared/schemas" -name "*.json" -type f | while read -r file; do
        update_file_contents "$file" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    done
    
    # Update Android manifest
    if [ -f "androidApp/src/main/AndroidManifest.xml" ]; then
        update_file_contents "androidApp/src/main/AndroidManifest.xml" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    fi
    
    # Update iOS Info.plist
    if [ -f "iosApp/$new_project_name/Info.plist" ]; then
        update_file_contents "iosApp/$new_project_name/Info.plist" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    fi
    
    print_success "Updated configuration files"
}

write_generated_readme() {
    local new_package="$1"
    local new_project_name="$2"
    local new_bundle_id="$3"

    cat > "README.md" << EOF
# $new_project_name

$new_project_name is a Kotlin Multiplatform app with native UI on Android and
iOS. Android uses Compose Multiplatform, iOS uses SwiftUI, and shared business
logic lives in the \`shared\` module.

## Project structure

\`\`\`text
androidApp/   Android UI and app configuration
iosApp/       SwiftUI UI, Xcode project, and iOS tests
shared/       Shared Kotlin logic, data, navigation, and state
\`\`\`

## Prerequisites

Install these tools before building the app:

- Android Studio or IntelliJ IDEA with Kotlin Multiplatform support
- Xcode 16 or later
- Java 21

The Gradle wrapper is included, so you don't need to install Gradle manually.

## Local configuration

Add local development values to \`local.properties\` or export them as
environment variables:

\`\`\`properties
SUPABASE_URL_DEV_AND=your-android-dev-supabase-url
SUPABASE_URL_DEV_IOS=your-ios-dev-supabase-url
SUPABASE_KEY_DEV=your-dev-supabase-key
SUPABASE_URL_PROD=your-prod-supabase-url
SUPABASE_KEY_PROD=your-prod-supabase-key
CONFIGCAT_AND_TEST_KEY=your-android-test-configcat-key
CONFIGCAT_AND_LIVE_KEY=your-android-live-configcat-key
CONFIGCAT_IOS_TEST_KEY=your-ios-test-configcat-key
CONFIGCAT_IOS_LIVE_KEY=your-ios-live-configcat-key
\`\`\`

Firebase configuration files are intentionally ignored by Git. Add your local
copies at these paths:

- \`androidApp/google-services.json\`
- \`iosApp/$new_project_name/GoogleService-Info.plist\`

The iOS Gradle build phase reads \`iosApp/.xcode.env\`, which discovers Java 21
when \`JAVA_HOME\` is unset. Use \`iosApp/.xcode.env.local\` for developer-specific
overrides.

## Build and test

Use these commands for local verification:

\`\`\`bash
./gradlew :androidApp:assembleDebug
./gradlew :shared:testAndroidHostTest
./gradlew :konsistTest:test
xcodebuild test -project iosApp/$new_project_name.xcodeproj -scheme $new_project_name -destination 'platform=iOS Simulator,name=iPhone 17' -configuration Debug
\`\`\`

## Identifiers

- Android package: \`$new_package\`
- iOS app bundle ID: \`$new_bundle_id.$new_project_name\`
- iOS test bundle ID: \`$new_bundle_id.${new_project_name}Tests\`
EOF
    print_success "Generated app README.md"
}

update_generated_branding() {
    local new_project_name="$1"

    find "androidApp/src/main/res" -name "strings.xml" -type f | while read -r file; do
        sed -i.tmp "s|<string name=\"app_name\">.*</string>|<string name=\"app_name\">$new_project_name</string>|g" "$file"
        rm -f "$file.tmp"
    done

    if [ -f "iosApp/$new_project_name/Localizable.xcstrings" ]; then
        sed -i.tmp "s|KMP Template|$new_project_name|g" "iosApp/$new_project_name/Localizable.xcstrings"
        rm -f "iosApp/$new_project_name/Localizable.xcstrings.tmp"
    fi

    print_success "Updated generated app branding"
}

create_xcode_env() {
    cat > "iosApp/.xcode.env" << 'EOF'
if [ -z "${JAVA_HOME:-}" ]; then
  export JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null)"
fi
EOF
    print_success "Created iosApp/.xcode.env"
}

create_release_note_placeholders() {
    local release_notes_dir="androidApp/release/whatsNew"

    mkdir -p "$release_notes_dir"
    printf 'Initial release notes placeholder.\n' > "$release_notes_dir/whatsnew-en-US"
    printf 'Notas de lanzamiento iniciales.\n' > "$release_notes_dir/whatsnew-es-419"
    printf 'Notas iniciais da versao.\n' > "$release_notes_dir/whatsnew-pt-BR"
    print_success "Created Android release-note placeholders"
}

cleanup_template_only_files() {
    rm -rf "docs/superpowers" 2>/dev/null || true
    rm -f "setup_new_project.sh" "SETUP_SCRIPT_README.md" 2>/dev/null || true
    rm -f "scripts/verify_setup_new_project.sh" 2>/dev/null || true
    cleanup_empty_directories "scripts"
    print_success "Removed template-only setup files"
}

# Function to update documentation files
update_documentation() {
    local old_package="$1"
    local new_package="$2"
    local old_project_name="$3"
    local new_project_name="$4"
    local old_bundle_id="$5"
    local new_bundle_id="$6"
    local old_domain="$7"
    local new_domain="$8"
    
    print_step "Updating documentation files..."
    
    # Update README.md
    update_file_contents "README.md" \
        "$old_package" "$new_package" \
        "$old_project_name" "$new_project_name" \
        "$old_bundle_id" "$new_bundle_id" \
        "$old_domain" "$new_domain"
    
    # Update all documentation files
    find "docs" -name "*.md" -type f | while read -r file; do
        update_file_contents "$file" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    done
    
    print_success "Updated documentation files"
}

# Function to update CI/CD workflows
update_workflows() {
    local old_package="$1"
    local new_package="$2"
    local old_project_name="$3"
    local new_project_name="$4"
    local old_bundle_id="$5"
    local new_bundle_id="$6"
    local old_domain="$7"
    local new_domain="$8"
    
    print_step "Updating CI/CD workflows..."
    
    # Update GitHub Actions workflows
    find ".github/workflows" -name "*.yml" -type f | while read -r file; do
        update_file_contents "$file" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    done
    
    # Update Dangerfile
    if [ -f "config/Dangerfile.df.kts" ]; then
        update_file_contents "config/Dangerfile.df.kts" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    fi
    
    print_success "Updated CI/CD workflows"
}

# Function to update other configuration files
update_other_configs() {
    local old_package="$1"
    local new_package="$2"
    local old_project_name="$3"
    local new_project_name="$4"
    local old_bundle_id="$5"
    local new_bundle_id="$6"
    local old_domain="$7"
    local new_domain="$8"
    
    print_step "Updating other configuration files..."
    
    # Update buildServer.json
    if [ -f "buildServer.json" ]; then
        update_file_contents "buildServer.json" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    fi
    
    # Update gradle.properties
    if [ -f "gradle.properties" ]; then
        update_file_contents "gradle.properties" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    fi
    
    # Update local.properties
    if [ -f "local.properties" ]; then
        update_file_contents "local.properties" \
            "$old_package" "$new_package" \
            "$old_project_name" "$new_project_name" \
            "$old_bundle_id" "$new_bundle_id" \
            "$old_domain" "$new_domain"
    fi
    
    print_success "Updated other configuration files"
}

# Function to create Firebase configuration files
create_firebase_configs() {
    local new_package="$1"
    local new_project_name="$2"
    local new_bundle_id="$3"
    
    print_step "Creating Firebase configuration files..."
    
    # Create google-services.json template
    create_google_services_json "$new_package"
    
    # Create GoogleService-Info.plist template
    create_google_service_info_plist "$new_bundle_id" "$new_project_name"
    
    print_success "Created Firebase configuration files"
}

# Function to create local.properties with API key placeholders
create_local_properties() {
    print_step "Creating local.properties with API key placeholders..."
    
    # Check if local.properties already exists
    if [ -f "local.properties" ]; then
        # Check if the placeholders are already added
        if ! grep -q "SUPABASE_URL_DEV_AND" "local.properties"; then
            # Append the placeholders to existing file
            cat >> "local.properties" << 'EOF'

###############################

# Supabase Development Credentials
SUPABASE_URL_DEV_AND=supabase-url-placeholder
SUPABASE_URL_DEV_IOS=supabase-url-placeholder
SUPABASE_KEY_DEV=supabase-key-placeholder

# Supabase Production Credentials
SUPABASE_URL_PROD=supabase-url-placeholder
SUPABASE_KEY_PROD=supabase-key-placeholder

# ConfigCat SDK Keys
CONFIGCAT_IOS_LIVE_KEY=configcat-key-placeholder
CONFIGCAT_IOS_TEST_KEY=configcat-key-placeholder
CONFIGCAT_AND_LIVE_KEY=configcat-key-placeholder
CONFIGCAT_AND_TEST_KEY=configcat-key-placeholder
EOF
            print_success "Added API key placeholders to existing local.properties"
        else
            print_success "API key placeholders already exist in local.properties"
        fi
    else
        # Create new local.properties file with placeholders
        cat > "local.properties" << 'EOF'
###############################

# Supabase Development Credentials
SUPABASE_URL_DEV_AND=supabase-url-placeholder
SUPABASE_URL_DEV_IOS=supabase-url-placeholder
SUPABASE_KEY_DEV=supabase-key-placeholder

# Supabase Production Credentials
SUPABASE_URL_PROD=supabase-url-placeholder
SUPABASE_KEY_PROD=supabase-key-placeholder

# ConfigCat SDK Keys
CONFIGCAT_IOS_LIVE_KEY=configcat-key-placeholder
CONFIGCAT_IOS_TEST_KEY=configcat-key-placeholder
CONFIGCAT_AND_LIVE_KEY=configcat-key-placeholder
CONFIGCAT_AND_TEST_KEY=configcat-key-placeholder
EOF
        print_success "Created local.properties with API key placeholders"
    fi
}

# Function to create google-services.json template
create_google_services_json() {
    local package_name="$1"
    
    local google_services_content='{
  "project_info": {
    "project_number": "123456789012",
    "firebase_url": "https://your-project-id.firebaseio.com",
    "project_id": "your-project-id",
    "storage_bucket": "your-project-id.appspot.com"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:123456789012:android:abcdef1234567890",
        "android_client_info": {
          "package_name": "'"$package_name"'"
        }
      },
      "oauth_client": [
        {
          "client_id": "123456789012-yourclientid.apps.googleusercontent.com",
          "client_type": 3
        }
      ],
      "api_key": [
        {
          "current_key": "A-replace-this-string-with-your-api-key"
        }
      ],
      "services": {
        "analytics_service": {
          "status": 2
        },
        "appinvite_service": {
          "status": 2,
          "other_platform_oauth_client": []
        },
        "ads_service": {
          "status": 1
        }
      }
    }
  ],
  "configuration_version": "1"
}'
    
    echo "$google_services_content" > "androidApp/google-services.json"
    print_success "Created google-services.json with package name: $package_name"
}

# Function to create GoogleService-Info.plist template
create_google_service_info_plist() {
    local bundle_id="$1"
    local project_name="$2"
    
    local plist_content='<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>API_KEY</key>
	<string>A-replace-this-string-with-your-api-key</string>
	<key>GCM_SENDER_ID</key>
	<string>123456789012</string>
	<key>PLIST_VERSION</key>
	<string>1</string>
	<key>BUNDLE_ID</key>
	<string>'"$bundle_id"."$project_name"'</string>
	<key>PROJECT_ID</key>
	<string>your-project-id</string>
	<key>STORAGE_BUCKET</key>
	<string>your-project-id.firebasestorage.app</string>
	<key>IS_ADS_ENABLED</key>
	<false></false>
	<key>IS_ANALYTICS_ENABLED</key>
	<false></false>
	<key>IS_APPINVITE_ENABLED</key>
	<true></true>
	<key>IS_GCM_ENABLED</key>
	<true></true>
	<key>IS_SIGNIN_ENABLED</key>
	<true></true>
	<key>GOOGLE_APP_ID</key>
	<string>1:123456789012:ios:abcdef1234567890</string>
</dict>
</plist>'
    
    echo "$plist_content" > "iosApp/$project_name/GoogleService-Info.plist"
    print_success "Created GoogleService-Info.plist with bundle ID: $bundle_id.$project_name"
}

# Function to clean up backup files
cleanup_backups() {
    print_step "Cleaning up backup files..."
    
    find . -name "*.backup" -type f -delete 2>/dev/null || true
    
    print_success "Cleaned up backup files"
}

# Function to validate the transformation
validate_transformation() {
    local new_package="$1"
    local new_project_name="$2"
    local new_bundle_id="$3"
    
    print_step "Validating transformation..."
    
    # Check if new directories exist
    local new_path=$(echo "$new_package" | tr '.' '/')
    local shared_source_sets=("commonMain" "androidMain" "iosMain" "commonTest")
    local all_dirs_exist=true
    
    # Check shared module directories
    for source_set in "${shared_source_sets[@]}"; do
        if [ -d "shared/src/$source_set/kotlin/$new_path" ]; then
            print_success "New shared/$source_set directory structure created successfully"
        else
            print_error "New shared/$source_set directory structure not found"
            all_dirs_exist=false
        fi
    done
    
    # Check composeApp directories
    if [ -d "androidApp/src/main/kotlin/$new_path" ]; then
        print_success "New androidApp directory structure created successfully"
    else
        print_error "New androidApp directory structure not found"
        all_dirs_exist=false
    fi

    if [ -d "androidApp/src/androidTest/kotlin/$new_path" ]; then
        print_success "New Android instrumentation test directory created successfully"
    else
        print_error "New Android instrumentation test directory not found"
        all_dirs_exist=false
    fi

    if [ -d "konsistTest/src/test/kotlin/$new_path" ]; then
        print_success "New Konsist test directory created successfully"
    else
        print_error "New Konsist test directory not found"
        all_dirs_exist=false
    fi

    if [ -d "shared/schemas/$new_package.db.AppDatabase" ]; then
        print_success "New Room schema directory created successfully"
    else
        print_error "New Room schema directory not found"
        all_dirs_exist=false
    fi
    
    if [ "$all_dirs_exist" = false ]; then
        return 1
    fi
    
    # Check if Xcode project was renamed
    if [ -d "iosApp/$new_project_name.xcodeproj" ]; then
        print_success "Xcode project renamed successfully"
    else
        print_error "Xcode project not renamed"
        return 1
    fi
    
    # Check if iOS app folder was renamed
    if [ -d "iosApp/$new_project_name" ]; then
        print_success "iOS app folder renamed successfully"
    else
        print_error "iOS app folder not renamed"
        return 1
    fi

    if [ -f "iosApp/${new_project_name}Tests/${new_project_name}Tests.swift" ]; then
        print_success "iOS root test file renamed successfully"
    else
        print_error "iOS root test file not renamed"
        return 1
    fi
    
    print_success "Transformation validation completed"
}

# Function to show final instructions
show_final_instructions() {
    local new_package="$1"
    local new_project_name="$2"
    local new_bundle_id="$3"
    
    echo ""
    echo -e "${GREEN}🎉 Project transformation completed successfully!${NC}"
    echo ""
    echo -e "${CYAN}📋 Next Steps:${NC}"
    echo ""
    echo -e "${YELLOW}1. Update your IDE:${NC}"
    echo "   - Close and reopen Android Studio"
    echo "   - Sync Gradle files"
    echo "   - Open the new Xcode project: iosApp/$new_project_name.xcodeproj"
    echo ""
    echo -e "${YELLOW}2. Update configuration files:${NC}"
    echo "   - Replace androidApp/google-services.json with your actual Firebase config"
    echo "   - Replace iosApp/$new_project_name/GoogleService-Info.plist with your actual Firebase config"
    echo "   - Update local.properties with your actual API keys (placeholders were added)"
    echo "   - Update GitHub repository owner/repo placeholders in config/Dangerfile.df.kts"
    echo "   - Note: Template files were created with correct package names and bundle IDs"
    echo ""
    echo -e "${YELLOW}3. Update GitHub repository:${NC}"
    echo "   - Update repository secrets in GitHub Settings"
    echo "   - Update workflow variables if needed"
    echo "   - Update repository name and description"
    echo ""
    echo -e "${YELLOW}4. Test your setup:${NC}"
    echo "   - Run: ./gradlew clean build"
    echo "   - Test Android build: ./gradlew :androidApp:assembleDebug"
    echo "   - Test shared Android host tests: ./gradlew :shared:testAndroidHostTest"
    echo "   - Test Konsist rules: ./gradlew :konsistTest:test"
    echo "   - Test iOS: xcodebuild test -project iosApp/$new_project_name.xcodeproj -scheme $new_project_name -destination 'platform=iOS Simulator,name=iPhone 17' -configuration Debug"
    echo ""
    echo -e "${CYAN}📊 Project Details:${NC}"
    echo "   Package Name: $new_package"
    echo "   Project Name: $new_project_name"
    echo "   App Bundle ID: $new_bundle_id.$new_project_name"
    echo "   Test Bundle ID: $new_bundle_id.${new_project_name}Tests"
    echo ""
    echo -e "${GREEN}Happy coding! 🚀${NC}"
}

# Main function
main() {
    echo -e "${PURPLE}=============================================================================${NC}"
    echo -e "${PURPLE}    Kotlin Multiplatform KMP-Template Project Setup Script${NC}"
    echo -e "${PURPLE}=============================================================================${NC}"
    echo ""
    
    # Current template values
    local OLD_PACKAGE="com.adriandeleon.kmp.template"
    local OLD_PROJECT_NAME="KMP-Template"
    local OLD_BUNDLE_ID="com.adriandeleon.kmp.template.KMPTemplate"
    local OLD_DOMAIN="adriandeleon"
    
    # Get user input
    echo -e "${CYAN}Please provide the following information for your new project:${NC}"
    echo ""
    
    # Get project name
    while true; do
        read -p "Enter your project name (e.g., MyAwesomeApp): " NEW_PROJECT_NAME_RAW
        NEW_PROJECT_NAME=$(to_project_name "$NEW_PROJECT_NAME_RAW")
        
        if validate_input "$NEW_PROJECT_NAME" "^[a-zA-Z][a-zA-Z0-9]*$" "Invalid project name. Must start with a letter and contain only alphanumeric characters"; then
            break
        fi
    done

    # Get package name
    while true; do
        read -p "Enter your package name (e.g., org.example.project): " NEW_PACKAGE_RAW
        NEW_PACKAGE=$(to_package_name "$NEW_PACKAGE_RAW")
        
        if validate_input "$NEW_PACKAGE" "^[a-z][a-z0-9]*(\.[a-z][a-z0-9]*)*$" "Invalid package name. Must be in format: com.yourcompany.yourapp"; then
            break
        fi
    done
    
    # Use the package name as the base for derived iOS bundle identifiers.
    NEW_BUNDLE_ID="$NEW_PACKAGE"
    
    # Extract domain from package name
    NEW_DOMAIN=$(get_domain "$NEW_PACKAGE")
    
    echo ""
    echo -e "${CYAN}📋 Configuration Summary:${NC}"
    echo "   Project Name: $NEW_PROJECT_NAME"
    echo "   Package Name: $NEW_PACKAGE"
    echo "   App Bundle ID: $NEW_BUNDLE_ID.$NEW_PROJECT_NAME"
    echo "   Test Bundle ID: $NEW_BUNDLE_ID.${NEW_PROJECT_NAME}Tests"
    echo "   Domain: $NEW_DOMAIN"
    echo ""
    
    # Confirm before proceeding
    read -p "Do you want to proceed with the transformation? (y/N): " confirm
    if [[ ! $confirm =~ ^[Yy]$ ]]; then
        print_info "Transformation cancelled by user"
        exit 0
    fi
    
    echo ""
    print_step "Starting project transformation..."
    
    # Create new directory structure
    create_directory_structure "$OLD_PACKAGE" "$NEW_PACKAGE"
    
    # Update all files
    update_source_files "$OLD_PACKAGE" "$NEW_PACKAGE" "$OLD_PROJECT_NAME" "$NEW_PROJECT_NAME" "$OLD_BUNDLE_ID" "$NEW_BUNDLE_ID" "$OLD_DOMAIN" "$NEW_DOMAIN"
    update_config_files "$OLD_PACKAGE" "$NEW_PACKAGE" "$OLD_PROJECT_NAME" "$NEW_PROJECT_NAME" "$OLD_BUNDLE_ID" "$NEW_BUNDLE_ID" "$OLD_DOMAIN" "$NEW_DOMAIN"
    update_documentation "$OLD_PACKAGE" "$NEW_PACKAGE" "$OLD_PROJECT_NAME" "$NEW_PROJECT_NAME" "$OLD_BUNDLE_ID" "$NEW_BUNDLE_ID" "$OLD_DOMAIN" "$NEW_DOMAIN"
    update_workflows "$OLD_PACKAGE" "$NEW_PACKAGE" "$OLD_PROJECT_NAME" "$NEW_PROJECT_NAME" "$OLD_BUNDLE_ID" "$NEW_BUNDLE_ID" "$OLD_DOMAIN" "$NEW_DOMAIN"
    update_other_configs "$OLD_PACKAGE" "$NEW_PACKAGE" "$OLD_PROJECT_NAME" "$NEW_PROJECT_NAME" "$OLD_BUNDLE_ID" "$NEW_BUNDLE_ID" "$OLD_DOMAIN" "$NEW_DOMAIN"
    
    # Update Xcode project (must be done after other updates)
    update_xcode_project "$OLD_PACKAGE" "$NEW_PACKAGE" "$OLD_PROJECT_NAME" "$NEW_PROJECT_NAME" "$OLD_BUNDLE_ID" "$NEW_BUNDLE_ID"
    
    # Create Firebase configuration files
    create_firebase_configs "$NEW_PACKAGE" "$NEW_PROJECT_NAME" "$NEW_BUNDLE_ID"
    
    # Create local.properties with API key placeholders
    create_local_properties

    # Generate project-specific docs, release metadata, and local Xcode env.
    write_generated_readme "$NEW_PACKAGE" "$NEW_PROJECT_NAME" "$NEW_BUNDLE_ID"
    update_generated_branding "$NEW_PROJECT_NAME"
    create_xcode_env
    create_release_note_placeholders
    
    # Remove old directories
    remove_old_directories "$OLD_PACKAGE"
    
    # Clean up backup files
    cleanup_backups

    # Remove generator-only files so stale template names are not left behind.
    cleanup_template_only_files
    
    # Validate transformation
    if validate_transformation "$NEW_PACKAGE" "$NEW_PROJECT_NAME" "$NEW_BUNDLE_ID"; then
        show_final_instructions "$NEW_PACKAGE" "$NEW_PROJECT_NAME" "$NEW_BUNDLE_ID"
    else
        print_error "Transformation validation failed. Please check the output above for errors."
        exit 1
    fi
}

# Run main function
main "$@"
