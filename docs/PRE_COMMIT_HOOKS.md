# Pre-commit Hooks

This document covers the pre-commit hooks setup that automatically ensures code quality before each commit.

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [🔧 What are Pre-commit Hooks?](#-what-are-pre-commit-hooks)
- [🚀 Setup](#-setup)
- [🪝 Available Hooks](#-available-hooks)
- [⚙️ Configuration](#-configuration)
- [📖 Usage](#-usage)
- [🐛 Troubleshooting](#-troubleshooting)

## 🎯 Overview

Pre-commit hooks are automated scripts that run before each commit to ensure code quality, consistency, and adherence to project standards. They help catch issues early and maintain a high-quality codebase.

## 🔧 What are Pre-commit Hooks?

Pre-commit hooks are Git hooks that automatically execute scripts before a commit is created. They can:

- **Format code** automatically
- **Run static analysis** tools
- **Execute tests** to ensure functionality
- **Check code style** and conventions
- **Validate commit messages** and metadata
- **Prevent commits** that don't meet quality standards

## 🚀 Setup

### Prerequisites

- Python 3.7+ installed
- Git repository initialized
- Project dependencies installed

### Installation

#### 1. Install pre-commit
```bash
# Using pip
pip install pre-commit

# Using Homebrew (macOS)
brew install pre-commit

# Verify installation
pre-commit --version
```

#### 2. Install Hooks
```bash
# Navigate to your project root
cd your-project

# Install the pre-commit hooks
pre-commit install

# Verify installation
pre-commit --version
```

### What Happens During Installation

When you run `pre-commit install`, the following occurs:

1. **Creates `.git/hooks/` directory** if it doesn't exist
2. **Installs hook scripts** that integrate with Git
3. **Downloads required tools** specified in configuration
4. **Sets up execution environment** for each hook

## 🪝 Available Hooks

The project includes several pre-commit hooks configured in `.pre-commit-config.yaml`:

### **Code Formatting Hooks**

#### Ktfmt (Kotlin)
- **Purpose**: Format Kotlin code according to official style guide
- **Trigger**: Before commit on Kotlin files
- **Action**: Automatically format code

#### SwiftFormat (Swift)
- **Purpose**: Format Swift code according to Swift style guidelines
- **Trigger**: Before commit on Swift files
- **Action**: Automatically format code

### **Code Quality Hooks**

#### Detekt (Kotlin)
- **Purpose**: Static code analysis for Kotlin
- **Trigger**: Before commit on Kotlin files
- **Action**: Analyze code quality and report issues

#### SwiftLint (Swift)
- **Purpose**: Static code analysis for Swift
- **Trigger**: Before commit on Swift files
- **Action**: Analyze code quality and report issues

### **General Quality Hooks**

#### Trailing Whitespace
- **Purpose**: Remove trailing whitespace
- **Trigger**: Before commit on all files
- **Action**: Automatically fix whitespace issues

#### End of Files
- **Purpose**: Ensure files end with newline
- **Trigger**: Before commit on all files
- **Action**: Add newline if missing

## ⚙️ Configuration

### Configuration File

The hooks are configured in `.pre-commit-config.yaml` in your project root:

```yaml
repos:
  # Kotlin formatting
  - repo: https://github.com/jguttman94/pre-commit-gradle
    rev: v0.3.0
    hooks:
    - id: gradle-task
      name: Ktfmt
      args: [--wrapper, ktfmtFormat]
      files: \.(kt|kts)$

  # Swift formatting
  - repo: https://github.com/nicklockwood/SwiftFormat
    rev: 0.55.4
    hooks:
    - id: swiftformat
      entry: swiftformat iosApp/ --config config/.swiftformat

  # General file cleanup
  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v4.4.0
    hooks:
    - id: trailing-whitespace
    - id: end-of-file-fixer
    - id: check-yaml
    - id: check-added-large-files
```

### Customizing Hooks

#### Add New Hooks
```yaml
# Add a new hook
- repo: https://github.com/example/hook-repo
  rev: v1.0.0
  hooks:
  - id: example-hook
    name: Example Hook
    description: Description of what this hook does
```

#### Modify Existing Hooks
```yaml
# Modify hook behavior
- id: trailing-whitespace
  args: [--markdown-linebreak-ext=md]  # Custom arguments
  exclude: ^(docs/|*.md)$             # Exclude specific files
```

#### Hook Execution Order
Hooks execute in the order they appear in the configuration file. Order them logically:

1. **File cleanup** (whitespace, end-of-file)
2. **Code formatting** (ktfmt, swiftformat)
3. **Code analysis** (detekt, swiftlint)
4. **Final validation** (tests, large files)

## 📖 Usage

### Automatic Execution

Once installed, hooks run automatically:

```bash
# Make changes to your code
git add .

# Commit changes (hooks run automatically)
git commit -m "Add new feature"

# If hooks fail, commit is blocked
# Fix issues and try again
```

### Manual Execution

#### Run All Hooks
```bash
# Run on all files
pre-commit run --all-files

# Run on staged files only
pre-commit run
```

#### Run Specific Hooks
```bash
# Run specific hook
pre-commit run ktfmt --all-files
pre-commit run swiftformat --all-files
pre-commit run detekt --all-files
```

#### Run on Specific Files
```bash
# Run on specific file
pre-commit run --files src/main/kotlin/MyFile.kt

# Run on specific file types
pre-commit run --files "*.kt"
```

### Hook Output

#### Successful Execution
```bash
Ktfmt................................................(no files to check) Skipped
SwiftFormat...........................................(no files to check) Skipped
trailing-whitespace..................................Passed
end-of-file-fixer...................................Passed
```

#### Failed Execution
```bash
Ktfmt................................................Failed
- hook id: gradle-task
- exit code: 1
- files were modified by this hook

# Fix issues and run again
pre-commit run ktfmt --all-files
```

## 🐛 Troubleshooting

### Common Issues

#### 1. **Hook Installation Failed**
```bash
# Check if hooks directory exists
ls -la .git/hooks/

# Reinstall hooks
pre-commit uninstall
pre-commit install
```

#### 2. **Hook Execution Failed**
```bash
# Check hook configuration
pre-commit run --verbose

# Verify required tools are installed
ktfmt --version
swiftformat --version
```

#### 3. **Performance Issues**
```bash
# Skip slow hooks during development
SKIP=detekt git commit -m "Quick commit"

# Run specific hooks only
pre-commit run ktfmt --all-files
```

#### 4. **Configuration Errors**
```bash
# Validate configuration
pre-commit run --all-files --verbose

# Check YAML syntax
python -c "import yaml; yaml.safe_load(open('.pre-commit-config.yaml'))"
```

### Debug Mode

#### Enable Verbose Logging
```bash
# Run with debug information
pre-commit run --all-files --verbose

# Check hook versions
pre-commit run --all-files --show-diff-on-failure
```

#### Skip Hooks Temporarily
```bash
# Skip all hooks
SKIP=all git commit -m "Skip hooks temporarily"

# Skip specific hooks
SKIP=detekt,swiftlint git commit -m "Skip analysis hooks"
```

## 📚 Best Practices

### 1. **Hook Organization**
- Group related hooks together
- Order hooks logically (cleanup → format → analyze → validate)
- Use descriptive names and descriptions

### 2. **Performance Optimization**
- Exclude generated files from hooks
- Use file type filters to limit hook scope
- Cache results when possible

### 3. **Team Collaboration**
- Document hook purposes and requirements
- Share configuration files across team
- Regular hook maintenance and updates

### 4. **Error Handling**
- Provide clear error messages
- Include fix instructions in hook output
- Use non-blocking hooks for warnings

### 5. **Maintenance**
- Regularly update hook versions
- Monitor hook performance
- Remove unused or redundant hooks

## 🔗 Related Documentation

- [Kotlin Format & Lint](KOTLIN_FORMAT_LINT.md) - Kotlin code quality tools
- [Swift Format & Lint](SWIFT_FORMAT_LINT.md) - Swift code quality tools
- [GitHub Actions Workflows](GITHUB_ACTIONS.md) - CI/CD integration
- [Code Coverage Reports](CODE_COVERAGE_REPORTS.md) - Testing coverage

## 📖 Resources

- [Pre-commit Documentation](https://pre-commit.com/)
- [Git Hooks Documentation](https://git-scm.com/docs/githooks)
- [Pre-commit Hooks Repository](https://github.com/pre-commit/pre-commit-hooks)
- [Gradle Pre-commit Plugin](https://github.com/jguttman94/pre-commit-gradle)

---

**Automate code quality with pre-commit hooks! 🚀**
