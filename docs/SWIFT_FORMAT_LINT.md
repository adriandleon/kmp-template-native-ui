# Swift Code Quality & Formatting

This document covers the Swift code quality tools used in this project: **SwiftFormat** for code formatting and **SwiftLint** for static code analysis.

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [✨ Code Formatting with SwiftFormat](#-code-formatting-with-swiftformat)
- [🔍 Static Analysis with SwiftLint](#-static-analysis-with-swiftlint)
- [🪝 Pre-commit Hooks](#-pre-commit-hooks)
- [🔄 CI/CD Integration](#-cicd-integration)
- [⚙️ Configuration](#️-configuration)
- [🐛 Troubleshooting](#-troubleshooting)

## 🎯 Overview

The project uses two main tools to maintain Swift code quality:

- **SwiftFormat** - Automatic Swift code formatter that follows Swift style guidelines
- **SwiftLint** - Static analysis tool that enforces Swift coding standards and best practices

## ✨ Code Formatting with SwiftFormat

### What is SwiftFormat?

[SwiftFormat](https://github.com/nicklockwood/SwiftFormat) is an automatic Swift code formatter that applies consistent formatting rules to your Swift code. It's highly configurable and can be integrated into your development workflow.

### Installation

#### Local Installation
```bash
# macOS
brew install swiftformat

# Verify installation
swiftformat --version
```

#### Xcode Integration
1. **Install SwiftFormat Xcode Extension**
   - Download from [App Store](https://apps.apple.com/app/swiftformat-for-xcode/id1400653534)
   - Or install via Xcode → Preferences → Extensions

2. **Configure Xcode to use SwiftFormat**
   - Xcode → Preferences → Text Editing → Format on Save
   - Select SwiftFormat as the formatter

### Configuration

Create a `.swiftformat` file in your project root:

```swift
# SwiftFormat Configuration
--indent 4                    # Use 4 spaces for indentation
--maxwidth 120               # Maximum line width
--wraparguments before-first # Wrap function arguments before first
--wrapparameters before-first # Wrap function parameters before first
--trimwhitespace always      # Always trim whitespace
--insertlines enabled        # Insert blank lines where appropriate
--removelines enabled        # Remove unnecessary blank lines
--disable redundanttype      # Don't add redundant type annotations
--disable redundantfileprivate # Don't add redundant fileprivate
--disable redundantpublic    # Don't add redundant public
```

### Usage

#### Manual Formatting
```bash
# Format all Swift files in project
swiftformat .

# Format specific file
swiftformat MyFile.swift

# Check formatting without changes
swiftformat --lint .

# Format with specific rules
swiftformat --rules indent,linebreaks .
```

#### IDE Integration
- **Xcode**: Format on save, manual formatting via Editor menu
- **VS Code**: Install SwiftFormat extension
- **Command Line**: Use in scripts and pre-commit hooks

## 🔍 Static Analysis with SwiftLint

### What is SwiftLint?

[SwiftLint](https://github.com/realm/SwiftLint) is a tool that enforces Swift style and conventions, helping maintain consistent code quality across your project.

### Installation

#### Local Installation
```bash
# macOS
brew install swiftlint

# Verify installation
swiftlint version
```

#### Xcode Integration
1. **Install SwiftLint Xcode Extension**
   - Download from [App Store](https://apps.apple.com/app/swiftlint-for-xcode/id1400653534)

2. **Configure Build Phases**
   - Add SwiftLint to your Xcode project's Build Phases
   - This ensures SwiftLint runs on every build

### Configuration

Create a `.swiftlint.yml` file in your project root:

```yaml
# SwiftLint Configuration
disabled_rules:
  - trailing_whitespace
  - line_length

opt_in_rules:
  - empty_count
  - force_unwrapping
  - implicitly_unwrapped_optional

included:
  - iosApp

excluded:
  - iosApp/Preview Content
  - iosApp/Generated

line_length:
  warning: 120
  error: 150

function_body_length:
  warning: 50
  error: 100

type_body_length:
  warning: 300
  error: 500

file_length:
  warning: 500
  error: 1000

cyclomatic_complexity:
  warning: 10
  error: 20
```

### Usage

#### Manual Analysis
```bash
# Lint all Swift files
swiftlint lint

# Lint specific file
swiftlint lint MyFile.swift

# Auto-correct issues where possible
swiftlint autocorrect

# Generate report
swiftlint lint --reporter html > swiftlint-report.html
```

#### IDE Integration
- **Xcode**: Real-time linting, build phase integration
- **VS Code**: Install SwiftLint extension
- **Command Line**: Use in scripts and pre-commit hooks

## 🪝 Pre-commit Hooks

### Automatic Code Quality

Pre-commit hooks automatically run Swift code quality checks before each commit, ensuring code meets quality standards.

### Setup
```bash
# Install pre-commit
pip install pre-commit

# Install hooks
pre-commit install
```

### What Runs Automatically
- **SwiftFormat formatting** - Ensures consistent code style
- **SwiftLint analysis** - Catches code quality issues
- **Other quality checks** - As configured in `.pre-commit-config.yaml`

### Manual Hook Execution
```bash
# Run all hooks on all files
pre-commit run --all-files

# Run specific hook
pre-commit run swiftformat --all-files
pre-commit run swiftlint --all-files
```

## 🚀 CI/CD Integration

### Automated Quality Checks

The project includes GitHub Actions workflows that automatically run Swift code quality checks on every Pull Request.

#### Workflow: `shared_test_lint.yml`
- **Trigger**: Pull Requests
- **Jobs**: 
  - Swift formatting check (SwiftFormat)
  - Swift static analysis (SwiftLint)
  - Unit tests
  - Danger checks

#### Configuration
The workflow is fully configurable with variables at the top. See [GitHub Actions Workflows](GITHUB_ACTIONS.md) for customization details.

### Local vs CI
- **Local**: Use pre-commit hooks for immediate feedback
- **CI**: Automated checks ensure quality on all PRs

## ⚙️ Configuration

### SwiftFormat Rules

| Rule | Description | Default |
|------|-------------|---------|
| `--indent` | Indentation spaces | `4` |
| `--maxwidth` | Maximum line width | `120` |
| `--wraparguments` | Function argument wrapping | `before-first` |
| `--trimwhitespace` | Whitespace trimming | `always` |
| `--insertlines` | Insert blank lines | `enabled` |

### SwiftLint Rules

| Category | Rules | Description |
|----------|-------|-------------|
| **Style** | `line_length`, `file_length` | Code structure limits |
| **Complexity** | `cyclomatic_complexity`, `function_body_length` | Code complexity metrics |
| **Best Practices** | `force_unwrapping`, `implicitly_unwrapped_optional` | Swift best practices |
| **Naming** | `identifier_name`, `type_name` | Naming conventions |

### Custom Rules

Add custom SwiftLint rules in `.swiftlint.yml`:

```yaml
custom_rules:
  no_force_unwrap_in_tests:
    name: "No Force Unwrap in Tests"
    regex: '(!+)'
    message: "Avoid force unwrapping in test files"
    severity: warning
    included:
      - "*Tests.swift"
      - "*Test.swift"
```

## 🐛 Troubleshooting

### Common Issues

#### SwiftFormat Issues
1. **Formatting conflicts**
   ```bash
   # Reset formatting and reformat
   swiftformat --lint . && swiftformat .
   ```

2. **Configuration not found**
   - Verify `.swiftformat` file exists in project root
   - Check file permissions

#### SwiftLint Issues
1. **Configuration not found**
   - Verify `.swiftlint.yml` exists
   - Check YAML syntax validity

2. **Rule conflicts**
   - Review `.swiftlint.yml` for conflicting rules
   - Check rule exclusions for generated files

3. **Performance issues**
   - Exclude generated files from analysis
   - Use specific file patterns in `included` section

### Debug Mode

#### Enable Verbose Logging
```bash
# SwiftFormat debug
swiftformat --verbose .

# SwiftLint debug
swiftlint lint --verbose
```

#### Check Configuration
```bash
# Verify SwiftFormat configuration
swiftformat --config .swiftformat --lint .

# Verify SwiftLint configuration
swiftlint lint --config .swiftlint.yml
```

## 📚 Best Practices

### 1. **Consistent Formatting**
- Run SwiftFormat before committing
- Use pre-commit hooks for automation
- Configure IDE to format on save

### 2. **Quality Maintenance**
- Address SwiftLint warnings promptly
- Use custom rules for project-specific needs
- Regular code quality reviews

### 3. **Team Collaboration**
- Share configuration files
- Document custom rules
- Regular quality check reviews

### 4. **Performance**
- Exclude generated files from analysis
- Use specific file patterns for inclusion
- Cache analysis results when possible

### 5. **Swift-Specific Guidelines**
- Follow Apple's Swift API Design Guidelines
- Use SwiftLint's opt-in rules for best practices
- Maintain consistent naming conventions
- Handle optionals safely

## 🔗 Related Documentation

- [Pre-Commit Hooks](PRE_COMMIT_HOOKS.md) - Local automation setup
- [GitHub Actions Workflows](GITHUB_ACTIONS.md) - CI/CD configuration
- [Kotlin Format & Lint](KOTLIN_FORMAT_LINT.md) - Android code quality tools
- [Code Coverage Reports](CODE_COVERAGE_REPORTS.md) - Testing coverage

## 📖 Resources

- [SwiftFormat Documentation](https://github.com/nicklockwood/SwiftFormat)
- [SwiftLint Documentation](https://github.com/realm/SwiftLint)
- [Swift API Design Guidelines](https://www.swift.org/documentation/api-design-guidelines/)
- [Apple Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/)

---

**Maintain high Swift code quality with automated tools! 🚀**