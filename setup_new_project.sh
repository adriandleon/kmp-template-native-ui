#!/bin/bash

# =============================================================================
# Kotlin Multiplatform Template Project Setup Script
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

# Function to convert string to valid bundle identifier
to_bundle_id() {
    echo "$1" | tr '[:upper:]' '[:lower:]' | sed 's/[^a-z0-9.]//g' | sed 's/\.\.*/\./g' | sed 's/^\.\|\.$//g'
}

# Function to get domain from package name
get_domain() {
    echo "$1" | cut -d'.' -f1-2
}

# Function to get app name from package name
get_app_name() {
    echo "$1" | rev | cut -d'.' -f1 | rev
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
    
    # Create new directory structure for composeApp module
    if [ -d "composeApp/src/androidMain/kotlin/$old_path" ]; then
        mkdir -p "composeApp/src/androidMain/kotlin/$new_path"
        cp -r "composeApp/src/androidMain/kotlin/$old_path"/* "composeApp/src/androidMain/kotlin/$new_path/" 2>/dev/null || true
        print_success "Created composeApp module directory structure: $new_path"
    fi
    
    # Create new directory structure for androidUnitTest
    if [ -d "composeApp/src/androidUnitTest/kotlin/$old_path" ]; then
        mkdir -p "composeApp/src/androidUnitTest/kotlin/$new_path"
        cp -r "composeApp/src/androidUnitTest/kotlin/$old_path"/* "composeApp/src/androidUnitTest/kotlin/$new_path/" 2>/dev/null || true
        print_success "Created androidUnitTest directory structure: $new_path"
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
    
    # Remove old directories from composeApp module
    rm -rf "composeApp/src/androidMain/kotlin/$old_path" 2>/dev/null || true
    rm -rf "composeApp/src/androidUnitTest/kotlin/$old_path" 2>/dev/null || true
    
    # Clean up empty parent directories
    for source_set in "${shared_source_sets[@]}"; do
        cleanup_empty_directories "shared/src/$source_set/kotlin"
    done
    cleanup_empty_directories "composeApp/src/androidMain/kotlin"
    cleanup_empty_directories "composeApp/src/androidUnitTest/kotlin"
    
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
    
    if [ -f "$file" ]; then
        # Create backup
        cp "$file" "$file.backup"
        
        # Replace package names
        sed -i.tmp "s|$old_package|$new_package|g" "$file"
        
        # Replace project names
        sed -i.tmp "s|$old_project_name|$new_project_name|g" "$file"
        
        # Replace bundle identifiers
        sed -i.tmp "s|$old_bundle_id|$new_bundle_id|g" "$file"
        
        # Replace domain names
        sed -i.tmp "s|$old_domain|$new_domain|g" "$file"
        
        # Clean up temporary files
        rm -f "$file.tmp"
        
        print_success "Updated: $file"
    fi
}

# Function to update Xcode project
update_xcode_project() {
    local old_project_name="$1"
    local new_project_name="$2"
    local old_bundle_id="$3"
    local new_bundle_id="$4"
    
    print_step "Updating Xcode project..."
    
    # Update project.pbxproj
    if [ -f "iosApp/$old_project_name.xcodeproj/project.pbxproj" ]; then
        update_file_contents "iosApp/$old_project_name.xcodeproj/project.pbxproj" \
            "com.adriandeleon.template" "$new_bundle_id" \
            "$old_project_name" "$new_project_name" \
            "com.adriandeleon.template.Template" "$new_bundle_id.$new_project_name" \
            "adriandeleon" "$(echo $new_bundle_id | cut -d'.' -f1)"
        
        # Rename Xcode project directory
        mv "iosApp/$old_project_name.xcodeproj" "iosApp/$new_project_name.xcodeproj"
        print_success "Renamed Xcode project directory"
    fi
    
    # Update Config.xcconfig
    if [ -f "iosApp/Configuration/Config.xcconfig" ]; then
        update_file_contents "iosApp/Configuration/Config.xcconfig" \
            "com.adriandeleon.template" "$new_bundle_id" \
            "$old_project_name" "$new_project_name" \
            "com.adriandeleon.template.Template" "$new_bundle_id.$new_project_name" \
            "adriandeleon" "$(echo $new_bundle_id | cut -d'.' -f1)"
    fi
    
    # Rename iOS app folder
    if [ -d "iosApp/$old_project_name" ]; then
        mv "iosApp/$old_project_name" "iosApp/$new_project_name"
        print_success "Renamed iOS app folder"
    fi
    
    # Update Swift files in the new folder
    find "iosApp/$new_project_name" -name "*.swift" -type f | while read -r file; do
        update_file_contents "$file" \
            "com.adriandeleon.template" "$new_bundle_id" \
            "$old_project_name" "$new_project_name" \
            "com.adriandeleon.template.Template" "$new_bundle_id.$new_project_name" \
            "adriandeleon" "$(echo $new_bundle_id | cut -d'.' -f1)"
    done
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
    
    update_file_contents "composeApp/build.gradle.kts" \
        "$old_package" "$new_package" \
        "$old_project_name" "$new_project_name" \
        "$old_bundle_id" "$new_bundle_id" \
        "$old_domain" "$new_domain"
    
    update_file_contents "shared/build.gradle.kts" \
        "$old_package" "$new_package" \
        "$old_project_name" "$new_project_name" \
        "$old_bundle_id" "$new_bundle_id" \
        "$old_domain" "$new_domain"
    
    # Update Android manifest
    if [ -f "composeApp/src/androidMain/AndroidManifest.xml" ]; then
        update_file_contents "composeApp/src/androidMain/AndroidManifest.xml" \
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
    if [ -d "composeApp/src/androidMain/kotlin/$new_path" ]; then
        print_success "New composeApp directory structure created successfully"
    else
        print_error "New composeApp directory structure not found"
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
    echo "   - Update google-services.json with your Firebase project"
    echo "   - Update GoogleService-Info.plist with your Firebase project"
    echo "   - Update local.properties with your API keys"
    echo ""
    echo -e "${YELLOW}3. Update GitHub repository:${NC}"
    echo "   - Update repository secrets in GitHub Settings"
    echo "   - Update workflow variables if needed"
    echo "   - Update repository name and description"
    echo ""
    echo -e "${YELLOW}4. Test your setup:${NC}"
    echo "   - Run: ./gradlew clean build"
    echo "   - Test Android build: ./gradlew :composeApp:assembleDebug"
    echo "   - Test iOS build in Xcode"
    echo ""
    echo -e "${CYAN}📊 Project Details:${NC}"
    echo "   Package Name: $new_package"
    echo "   Project Name: $new_project_name"
    echo "   Bundle ID: $new_bundle_id"
    echo ""
    echo -e "${GREEN}Happy coding! 🚀${NC}"
}

# Main function
main() {
    echo -e "${PURPLE}=============================================================================${NC}"
    echo -e "${PURPLE}    Kotlin Multiplatform Template Project Setup Script${NC}"
    echo -e "${PURPLE}=============================================================================${NC}"
    echo ""
    
    # Current template values
    local OLD_PACKAGE="com.adriandeleon.template"
    local OLD_PROJECT_NAME="Template"
    local OLD_BUNDLE_ID="com.adriandeleon.template.Template"
    local OLD_DOMAIN="adriandeleon"
    
    # Get user input
    echo -e "${CYAN}Please provide the following information for your new project:${NC}"
    echo ""
    
    # Get package name
    while true; do
        read -p "Enter your package name (e.g., com.yourcompany.yourapp): " NEW_PACKAGE_RAW
        NEW_PACKAGE=$(to_package_name "$NEW_PACKAGE_RAW")
        
        if validate_input "$NEW_PACKAGE" "^[a-z][a-z0-9]*(\.[a-z][a-z0-9]*)*$" "Invalid package name. Must be in format: com.yourcompany.yourapp"; then
            break
        fi
    done
    
    # Get project name
    while true; do
        read -p "Enter your project name (e.g., MyAwesomeApp): " NEW_PROJECT_NAME_RAW
        NEW_PROJECT_NAME=$(to_project_name "$NEW_PROJECT_NAME_RAW")
        
        if validate_input "$NEW_PROJECT_NAME" "^[a-zA-Z][a-zA-Z0-9]*$" "Invalid project name. Must start with a letter and contain only alphanumeric characters"; then
            break
        fi
    done
    
    # Generate bundle identifier from package name and project name
    NEW_BUNDLE_ID="$NEW_PACKAGE.$NEW_PROJECT_NAME"
    
    echo -e "${CYAN}Generated bundle identifier: $NEW_BUNDLE_ID${NC}"
    echo -e "${YELLOW}Note: iOS bundle identifier format is: packageName.projectName${NC}"
    echo ""
    
    # Ask if user wants to customize the bundle identifier
    read -p "Do you want to customize the bundle identifier? (y/N): " customize_bundle
    if [[ $customize_bundle =~ ^[Yy]$ ]]; then
        while true; do
            read -p "Enter your custom bundle identifier (e.g., com.yourcompany.yourapp): " NEW_BUNDLE_ID_RAW
            NEW_BUNDLE_ID=$(to_bundle_id "$NEW_BUNDLE_ID_RAW")
            
            if validate_input "$NEW_BUNDLE_ID" "^[a-z][a-z0-9]*(\.[a-z][a-z0-9]*)*$" "Invalid bundle identifier. Must be in format: com.yourcompany.yourapp"; then
                break
            fi
        done
    fi
    
    # Extract domain from package name
    NEW_DOMAIN=$(get_domain "$NEW_PACKAGE")
    
    echo ""
    echo -e "${CYAN}📋 Configuration Summary:${NC}"
    echo "   Package Name: $NEW_PACKAGE"
    echo "   Project Name: $NEW_PROJECT_NAME"
    echo "   Bundle ID: $NEW_BUNDLE_ID"
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
    update_xcode_project "$OLD_PROJECT_NAME" "$NEW_PROJECT_NAME" "$OLD_BUNDLE_ID" "$NEW_BUNDLE_ID"
    
    # Remove old directories
    remove_old_directories "$OLD_PACKAGE"
    
    # Clean up backup files
    cleanup_backups
    
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
