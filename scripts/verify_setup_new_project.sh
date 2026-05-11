#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_NAME="${1:-SampleApp}"
PACKAGE_NAME="${2:-org.example.sample}"
MODULE_NAME="${PROJECT_NAME//-/_}"
TMP_DIR="$(mktemp -d "${TMPDIR:-/tmp}/kmp-template-setup.XXXXXX")"

cleanup() {
    rm -rf "$TMP_DIR"
}
trap cleanup EXIT

copy_template() {
    rsync -a \
        --exclude '.git' \
        --exclude '.gradle' \
        --exclude 'build' \
        --exclude '*/build' \
        --exclude '.kotlin' \
        --exclude '.idea' \
        "$ROOT_DIR/" "$TMP_DIR/project/"
}

run_setup() {
    cd "$TMP_DIR/project"
    printf '%s\n%s\ny\n' "$PROJECT_NAME" "$PACKAGE_NAME" | bash ./setup_new_project.sh >/tmp/kmp-template-setup.log
}

assert_file_exists() {
    local path="$1"

    if [ ! -f "$path" ]; then
        echo "Expected file to exist: $path" >&2
        return 1
    fi
}

assert_dir_exists() {
    local path="$1"

    if [ ! -d "$path" ]; then
        echo "Expected directory to exist: $path" >&2
        return 1
    fi
}

assert_no_match() {
    local pattern="$1"
    local message="$2"

    if rg -n "$pattern" . >/tmp/kmp-template-match.log; then
        echo "$message" >&2
        cat /tmp/kmp-template-match.log >&2
        return 1
    fi
}

assert_match() {
    local pattern="$1"
    local path="$2"

    if ! rg -n "$pattern" "$path" >/dev/null; then
        echo "Expected pattern '$pattern' in $path" >&2
        return 1
    fi
}

copy_template
run_setup

assert_file_exists "iosApp/${PROJECT_NAME}Tests/${PROJECT_NAME}Tests.swift"
assert_file_exists "iosApp/.xcode.env"
assert_file_exists "androidApp/release/whatsNew/whatsnew-en-US"
assert_file_exists "androidApp/release/whatsNew/whatsnew-es-419"
assert_file_exists "androidApp/release/whatsNew/whatsnew-pt-BR"

assert_dir_exists "androidApp/src/androidTest/kotlin/org/example/sample"
assert_dir_exists "konsistTest/src/test/kotlin/org/example/sample"
assert_dir_exists "shared/schemas/org.example.sample.db.AppDatabase"

assert_no_match "KMP_Template|KMP Template|com\\.adriandeleon\\.kmp\\.template|JAVA_VERSION: '17'|SUPABASE_URL_DEV_(AND|IOS): \\$\\{\\{ secrets\\.SUPABASE_URL_PROD|SUPABASE_KEY_DEV: \\$\\{\\{ secrets\\.SUPABASE_KEY_PROD" \
    "Generated project still contains stale template references, stale Java config, or dev secrets mapped to prod secrets."

assert_match "@testable import ${MODULE_NAME}" "iosApp/${PROJECT_NAME}Tests"
assert_match "struct ${PROJECT_NAME}Tests" "iosApp/${PROJECT_NAME}Tests/${PROJECT_NAME}Tests.swift"
assert_match "PRODUCT_BUNDLE_IDENTIFIER=${PACKAGE_NAME}.${PROJECT_NAME}$" "iosApp/Configuration/Config.xcconfig"
assert_match "DEVELOPMENT_TEAM = \"\\$\\(TEAM_ID\\)\"" "iosApp/${PROJECT_NAME}.xcodeproj/project.pbxproj"
assert_match "PROJECT_NAME = \"${PROJECT_NAME}\"" "config/Dangerfile.df.kts"
assert_match "ANDROID_MODULE_PATH = \"androidApp/src/main/\"" "config/Dangerfile.df.kts"
assert_match "IOS_MODULE_PATH = \"iosApp/${PROJECT_NAME}/\"" "config/Dangerfile.df.kts"
assert_match "KermitLogger.setTag\\(\"${PROJECT_NAME}\"\\)" "shared/src/commonMain/kotlin/org/example/sample/logger/LoggerModule.kt"
assert_match "<string name=\"app_name\">${PROJECT_NAME}</string>" "androidApp/src/main/res/values/strings.xml"
assert_match "\"${PROJECT_NAME}\"" "iosApp/${PROJECT_NAME}/Localizable.xcstrings"

echo "Generated project verification passed for $PROJECT_NAME ($PACKAGE_NAME)."
