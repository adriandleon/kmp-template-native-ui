# Code Coverage Reports

This document covers code coverage configuration, reporting, and analysis using Kover for Kotlin Multiplatform projects.

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [📊 What is Code Coverage?](#-what-is-code-coverage)
- [🔧 Kover Integration](#-kover-integration)
- [⚙️ Configuration](#️-configuration)
- [🏃 Running Coverage](#-running-coverage)
- [📈 Coverage Reports](#-coverage-reports)
- [🎯 Coverage Goals](#-coverage-goals)
- [🔄 CI/CD Integration](#-cicd-integration)
- [📚 Best Practices](#-best-practices)
- [🐛 Troubleshooting](#-troubleshooting)

## 🎯 Overview

Code coverage is a metric that measures how much of your source code is executed during testing. This project uses **Kover** to generate comprehensive coverage reports for Kotlin Multiplatform modules.

## 📊 What is Code Coverage?

Code coverage helps you understand:

- **How much code** is tested by your test suite
- **Which parts** of your code need more testing
- **Quality metrics** for your test coverage
- **Gaps** in your testing strategy

### **Coverage Types**

- **Line Coverage** - Percentage of code lines executed
- **Branch Coverage** - Percentage of conditional branches executed
- **Function Coverage** - Percentage of functions called
- **Class Coverage** - Percentage of classes instantiated

## 🚀 Kover Integration

### **What is Kover?**

[Kover](https://github.com/Kotlin/kotlinx-kover) is a Kotlin Multiplatform code coverage tool that:

- **Integrates seamlessly** with Kotlin projects
- **Supports all KMP targets** (Android, iOS, JVM, JS, Native)
- **Generates multiple report formats** (HTML, XML, JSON)
- **Integrates with CI/CD** pipelines
- **Provides detailed coverage analysis**

### **Installation**

#### Add Kover Plugin
```kotlin
// In shared/build.gradle.kts
plugins {
    id("org.jetbrains.kotlinx.kover")
}
```

#### Add Dependencies
```kotlin
dependencies {
    // Kover is included with the plugin
    // No additional dependencies needed
}
```

## ⚙️ Configuration

### **Basic Configuration**

```kotlin
// In shared/build.gradle.kts
kover {
    reports {
        html {
            setEnabled(true)
        }
        xml {
            setEnabled(true)
        }
        json {
            setEnabled(true)
        }
    }
}
```

### **Advanced Configuration**

#### Coverage Filters
```kotlin
kover {
    filters {
        classes {
            // Include specific packages
            includes += listOf(
                "com.yourcompany.yourapp.domain.*",
                "com.yourcompany.yourapp.data.*"
            )
            
            // Exclude generated or test files
            excludes += listOf(
                "com.yourcompany.yourapp.generated.*",
                "*MapperImpl",
                "*ComponentImpl",
                "*Test",
                "*Tests"
            )
        }
        
        files {
            // Include specific file patterns
            includes += listOf("**/*.kt")
            
            // Exclude specific file patterns
            excludes += listOf(
                "**/generated/**",
                "**/test/**",
                "**/*Test.kt"
            )
        }
    }
}
```

#### Report Configuration
```kotlin
kover {
    reports {
        html {
            setEnabled(true)
            setTitle("Code Coverage Report")
            setDir(layout.buildDirectory.dir("reports/kover/html"))
        }
        
        xml {
            setEnabled(true)
            setDir(layout.buildDirectory.dir("reports/kover/xml"))
        }
        
        json {
            setEnabled(true)
            setDir(layout.buildDirectory.dir("reports/kover/json"))
        }
        
        lcov {
            setEnabled(true)
            setDir(layout.buildDirectory.dir("reports/kover/lcov"))
        }
    }
}
```

#### Coverage Verification
```kotlin
kover {
    verify {
        rule {
            bound {
                minValue = 80.0  // Minimum coverage percentage
            }
        }
    }
}
```

## 🏃‍♂️ Running Coverage

### **Command Line Execution**

#### Generate Coverage Reports
```bash
# Generate HTML coverage report
./gradlew :shared:koverHtmlReport

# Generate XML coverage report
./gradlew :shared:koverXmlReport

# Generate JSON coverage report
./gradlew :shared:koverJsonReport

# Generate LCOV coverage report
./gradlew :shared:koverLcovReport

# Generate all coverage reports
./gradlew :shared:koverReport
```

#### Run Tests with Coverage
```bash
# Run tests and generate coverage
./gradlew :shared:testDebugUnitTest

# Run specific tests with coverage
./gradlew :shared:testDebugUnitTest --tests "*UserEntityTest*"
```

#### Coverage Verification
```bash
# Verify coverage meets minimum thresholds
./gradlew :shared:koverVerify

# Run tests and verify coverage
./gradlew :shared:testDebugUnitTest koverVerify
```

### **IDE Integration**

#### Android Studio
1. **Run with Coverage**: Right-click on test → Run with Coverage
2. **View Coverage**: View → Tool Windows → Coverage
3. **Coverage Report**: Build → Generate Coverage Report

#### VS Code
1. **Install Kotlin extension**
2. **Run tests with coverage** via command palette
3. **View coverage** in output panel

## 📈 Coverage Reports

### **HTML Report**

The HTML report provides an interactive coverage overview:

#### Features
- **File tree navigation** showing coverage by package
- **Line-by-line coverage** highlighting covered/uncovered lines
- **Coverage summary** with percentages and statistics
- **Search and filter** capabilities
- **Export options** for sharing

#### Access
```bash
# Generate HTML report
./gradlew :shared:koverHtmlReport

# Open in browser
open shared/build/reports/kover/html/index.html
```

### **XML Report**

The XML report is useful for CI/CD integration:

#### Features
- **Machine-readable format** for automation
- **Detailed coverage data** for analysis
- **Integration** with coverage tools
- **Standard format** for reporting

#### Access
```bash
# Generate XML report
./gradlew :shared:koverXmlReport

# Location
shared/build/reports/kover/xml/coverage.xml
```

### **JSON Report**

The JSON report provides structured coverage data:

#### Features
- **Structured data format** for processing
- **Easy integration** with custom tools
- **Detailed metrics** and statistics
- **API-friendly format**

#### Access
```bash
# Generate JSON report
./gradlew :shared:koverJsonReport

# Location
shared/build/reports/kover/json/coverage.json
```

### **LCOV Report**

The LCOV report is compatible with many coverage tools:

#### Features
- **Standard LCOV format** for compatibility
- **Integration** with coverage services
- **Line coverage details** for analysis
- **Widely supported** format

#### Access
```bash
# Generate LCOV report
./gradlew :shared:koverLcovReport

# Location
shared/build/reports/kover/lcov/lcov.info
```

## 🎯 Coverage Goals

### **Recommended Coverage Targets**

- **Overall Coverage**: 90% minimum
- **Critical Business Logic**: 100% coverage
- **Data Layer**: 95% coverage
- **Domain Layer**: 90% coverage
- **Presentation Layer**: 85% coverage
- **Platform-Specific**: 80% coverage

### **Coverage by Module**

#### Shared Module
- **Domain Logic**: 95% minimum
- **Data Operations**: 90% minimum
- **Utility Functions**: 85% minimum

#### Android Module
- **UI Components**: 80% minimum
- **Platform Logic**: 85% minimum
- **Integration**: 90% minimum

#### iOS Module
- **UI Components**: 80% minimum
- **Platform Logic**: 85% minimum
- **Integration**: 90% minimum

### **Setting Coverage Thresholds**

```kotlin
kover {
    verify {
        rule {
            bound {
                minValue = 90.0  // Overall minimum
            }
        }
        
        rule {
            bound {
                minValue = 95.0  // Domain layer minimum
                includes = listOf("**/domain/**")
            }
        }
        
        rule {
            bound {
                minValue = 100.0  // Critical functions
                includes = listOf("**/CriticalFunction.kt")
            }
        }
    }
}
```

## 🚀 CI/CD Integration

### **GitHub Actions Integration**

#### Coverage Job
```yaml
# In .github/workflows/shared_test_lint.yml
- name: Generate Coverage Report
  run: ./gradlew :shared:koverHtmlReport

- name: Upload Coverage Report
  uses: actions/upload-artifact@v3
  with:
    name: coverage-report
    path: shared/build/reports/kover/html/
```

#### Coverage Verification
```yaml
- name: Verify Coverage
  run: ./gradlew :shared:koverVerify
```

### **Coverage Badges**

#### Codecov Integration
```yaml
- name: Upload to Codecov
  uses: codecov/codecov-action@v3
  with:
    file: shared/build/reports/kover/xml/coverage.xml
    format: cobertura
```

#### Custom Badge Generation
```yaml
- name: Generate Coverage Badge
  run: |
    COVERAGE=$(./gradlew :shared:koverVerify --quiet | grep -o '[0-9.]*%' | head -1)
    echo "Coverage: $COVERAGE"
```

## 📚 Best Practices

### **1. Coverage Strategy**
- **Focus on business logic** rather than generated code
- **Test critical paths** with 100% coverage
- **Accept lower coverage** for UI components
- **Set realistic targets** based on project complexity

### **2. Test Organization**
- **Group related tests** for better coverage analysis
- **Use descriptive test names** that explain coverage
- **Maintain test data** that exercises all code paths
- **Regular coverage reviews** to identify gaps

### **3. Coverage Analysis**
- **Review uncovered lines** to understand why
- **Identify dead code** that can be removed
- **Focus on high-impact areas** for improvement
- **Use coverage trends** to track progress

### **4. CI/CD Integration**
- **Automate coverage generation** on every build
- **Set coverage thresholds** to prevent regression
- **Generate coverage reports** for review
- **Track coverage trends** over time

### **5. Team Collaboration**
- **Share coverage reports** with the team
- **Set team coverage goals** and track progress
- **Review coverage gaps** during code reviews
- **Celebrate coverage improvements** and milestones

## 🐛 Troubleshooting

### **Common Issues**

#### 1. **Coverage Not Generated**
```bash
# Check if tests ran successfully
./gradlew :shared:test --info

# Verify Kover plugin is applied
./gradlew :shared:plugins
```

#### 2. **Coverage Reports Empty**
```bash
# Check test execution
./gradlew :shared:testDebugUnitTest --info

# Verify source sets
./gradlew :shared:sourceSets
```

#### 3. **Coverage Verification Failed**
```bash
# Check current coverage
./gradlew :shared:koverHtmlReport

# Review coverage report
open shared/build/reports/kover/html/index.html
```

### **Debug Mode**

#### Enable Verbose Logging
```bash
# Run with debug information
./gradlew :shared:koverHtmlReport --debug

# Check Kover configuration
./gradlew :shared:koverHtmlReport --scan
```

#### Coverage Analysis
```bash
# Generate detailed coverage
./gradlew :shared:koverReport

# Check coverage filters
./gradlew :shared:koverHtmlReport --info
```

## 🔗 Related Documentation

- [Unit Tests Shared](UNIT_TESTS_SHARED.md) - Testing strategies and tools
- [GitHub Actions Workflows](GITHUB_ACTIONS.md) - CI/CD integration
- [Kotlin Format & Lint](KOTLIN_FORMAT_LINT.md) - Code quality tools
- [Pre-Commit Hooks](PRE_COMMIT_HOOKS.md) - Local automation

## 📖 Resources

- [Kover Documentation](https://github.com/Kotlin/kotlinx-kover)
- [Kotlin Testing Guide](https://kotlinlang.org/docs/testing.html)
- [Code Coverage Best Practices](https://martinfowler.com/bliki/TestCoverage.html)
- [Coverage Tools Comparison](https://en.wikipedia.org/wiki/Code_coverage)

---

**Track and improve your code quality with comprehensive coverage! 🚀**