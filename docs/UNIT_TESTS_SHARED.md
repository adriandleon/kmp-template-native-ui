# Unit Testing in Kotlin Multiplatform

This document covers unit testing strategies and tools used in the Kotlin Multiplatform project, focusing on shared module testing and platform-specific test implementations.

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [🧪 Testing Strategy](#-testing-strategy)
- [🛠️ Testing Tools](#-testing-tools)
- [📁 Test Structure](#-test-structure)
- [✍️ Writing Tests](#-writing-tests)
- [🏃 Running Tests](#-running-tests)
- [📊 Test Coverage](#-test-coverage)
- [📚 Best Practices](#-best-practices)
- [🐛 Troubleshooting](#-troubleshooting)

## 🎯 Overview

The project uses a comprehensive testing strategy that ensures code quality across all platforms:

- **Shared Module Tests** - Common business logic testing
- **Platform-Specific Tests** - Android and iOS specific implementations
- **Integration Tests** - Cross-platform functionality validation
- **Test Coverage** - Automated coverage reporting and analysis

## 🧪 Testing Strategy

### **Multi-Layer Testing Approach**

1. **Unit Tests** - Test individual functions and classes in isolation
2. **Integration Tests** - Test interactions between components
3. **Platform Tests** - Test platform-specific implementations
4. **Coverage Tests** - Ensure comprehensive test coverage

### **Test Organization**

```
shared/
├── src/
│   ├── commonMain/           # Shared business logic
│   ├── commonTest/           # Shared unit tests
│   ├── androidMain/          # Android implementations
│   ├── androidTest/          # Android-specific tests
│   ├── iosMain/              # iOS implementations
│   └── iosTest/              # iOS-specific tests
```

## 🛠️ Testing Tools

### **Primary Testing Framework**

#### Kotest
[Kotest](https://kotest.io/) is the primary testing framework, providing:

- **Multiple testing styles** (StringSpec, BehaviorSpec, FunSpec)
- **Property-based testing** with generators
- **Matchers** for assertions
- **Test lifecycle hooks** for setup and teardown
- **Parallel test execution** for performance

#### Installation
```kotlin
// In shared/build.gradle.kts
dependencies {
    commonTestImplementation(libs.kotest.framework.engine)
    commonTestImplementation(libs.kotest.assertions.core)
    commonTestImplementation(libs.kotest.property)
}
```

### **Mocking Framework**

#### Mokkery
[Mokkery](https://github.com/mockk/mokkery) provides mocking capabilities:

- **Mock creation** for interfaces and classes
- **Stubbing** for method calls
- **Verification** of method invocations
- **Argument matching** and capture

#### Installation
```kotlin
// In shared/build.gradle.kts
dependencies {
    commonTestImplementation(libs.mokkery.runtime)
}
```

### **Additional Testing Libraries**

#### Coroutines Testing
```kotlin
dependencies {
    commonTestImplementation(libs.kotlinx.coroutines.test)
}
```

#### DateTime Testing
```kotlin
dependencies {
    commonTestImplementation(libs.kotlinx.datetime.test)
}
```

## 📁 Test Structure

### **Shared Module Test Structure**

```
shared/src/commonTest/kotlin/
├── com/yourcompany/yourapp/
│   ├── domain/
│   │   ├── entity/
│   │   │   └── UserEntityTest.kt
│   │   ├── repository/
│   │   │   └── UserRepositoryTest.kt
│   │   └── usecase/
│   │       └── GetUserUseCaseTest.kt
│   ├── data/
│   │   ├── datasource/
│   │   │   └── UserDataSourceTest.kt
│   │   ├── mapper/
│   │   │   └── UserMapperTest.kt
│   │   └── repository/
│   │       └── UserRepositoryImplTest.kt
│   └── presentation/
│       ├── component/
│       │   └── HomeComponentTest.kt
│       └── store/
│           └── HomeStoreTest.kt
```

### **Test File Naming Convention**

- **Test files**: `{ClassName}Test.kt`
- **Test classes**: `{ClassName}Test`
- **Test functions**: Descriptive names using backticks

## ✍️ Writing Tests

### **Basic Test Structure**

#### Using StringSpec Style
```kotlin
class UserEntityTest : StringSpec({
    "should create user with valid data" {
        // Given
        val id = "user123"
        val name = "John Doe"
        val email = "john@example.com"
        
        // When
        val user = UserEntity(id = id, name = name, email = email)
        
        // Then
        user.id shouldBe id
        user.name shouldBe name
        user.email shouldBe email
    }
    
    "should validate email format" {
        // Given
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org"
        )
        
        val invalidEmails = listOf(
            "invalid-email",
            "@example.com",
            "user@",
            "user@.com"
        )
        
        // When & Then
        validEmails.forEach { email ->
            UserEntity.isValidEmail(email) shouldBe true
        }
        
        invalidEmails.forEach { email ->
            UserEntity.isValidEmail(email) shouldBe false
        }
    }
})
```

#### Using BehaviorSpec Style
```kotlin
class UserRepositoryTest : BehaviorSpec({
    given("a user repository") {
        val mockDataSource = mockk<UserDataSource>()
        val repository = UserRepositoryImpl(mockDataSource)
        
        `when`("fetching a user by ID") {
            val userId = "user123"
            val expectedUser = UserEntity(id = userId, name = "John Doe")
            
            every { mockDataSource.getUser(userId) } returns expectedUser
            
            then("it should return the user") {
                val result = repository.getUser(userId)
                result shouldBe expectedUser
            }
            
            then("it should call the data source") {
                verify { mockDataSource.getUser(userId) }
            }
        }
        
        `when`("user is not found") {
            val userId = "nonexistent"
            
            every { mockDataSource.getUser(userId) } throws UserNotFoundException()
            
            then("it should throw an exception") {
                shouldThrow<UserNotFoundException> {
                    repository.getUser(userId)
                }
            }
        }
    }
})
```

### **Property-Based Testing**

```kotlin
class UserValidationTest : PropertySpec({
    property("valid emails should pass validation") {
        forAll(Arb.string().filter { it.contains("@") }) { email ->
            UserEntity.isValidEmail(email) shouldBe true
        }
    }
    
    property("user ID should be non-empty") {
        forAll(Arb.string().filter { it.isNotEmpty() }) { id ->
            UserEntity(id = id, name = "Test", email = "test@example.com").id shouldBe id
        }
    }
})
```

### **Testing Coroutines**

```kotlin
class AsyncUserUseCaseTest : StringSpec({
    "should execute use case successfully" {
        runTest {
            // Given
            val mockRepository = mockk<UserRepository>()
            val useCase = GetUserUseCase(mockRepository)
            val expectedUser = UserEntity(id = "user123", name = "John Doe")
            
            coEvery { mockRepository.getUser("user123") } returns expectedUser
            
            // When
            val result = useCase.execute("user123")
            
            // Then
            result shouldBe expectedUser
            coVerify { mockRepository.getUser("user123") }
        }
    }
    
    "should handle repository errors" {
        runTest {
            // Given
            val mockRepository = mockk<UserRepository>()
            val useCase = GetUserUseCase(mockRepository)
            val expectedError = UserNotFoundException()
            
            coEvery { mockRepository.getUser("user123") } throws expectedError
            
            // When & Then
            shouldThrow<UserNotFoundException> {
                useCase.execute("user123")
            }
        }
    }
})
```

## 🚀 Running Tests

### **Command Line Execution**

#### Run All Tests
```bash
# Run all tests in the project
./gradlew test

# Run tests for specific module
./gradlew :shared:test
./gradlew :composeApp:test
```

#### Run Specific Tests
```bash
# Run tests matching a pattern
./gradlew :shared:test --tests "*UserEntityTest*"

# Run specific test class
./gradlew :shared:test --tests "com.yourcompany.yourapp.domain.entity.UserEntityTest"

# Run specific test function
./gradlew :shared:test --tests "*should create user with valid data*"
```

#### Test Execution Options
```bash
# Run tests in parallel
./gradlew :shared:test --parallel

# Run tests with debug output
./gradlew :shared:test --debug

# Run tests with info logging
./gradlew :shared:test --info
```

### **IDE Integration**

#### Android Studio
1. **Run individual tests**: Right-click on test function → Run
2. **Run test class**: Right-click on test class → Run
3. **Run all tests**: Right-click on test directory → Run Tests

#### VS Code
1. **Install Kotlin extension**
2. **Use Test Explorer** for test execution
3. **Run tests** via command palette

### **Continuous Integration**

Tests run automatically in GitHub Actions:

```yaml
# In .github/workflows/shared_test_lint.yml
- name: Run Unit Tests
  run: ./gradlew test
```

## 📊 Test Coverage

### **Coverage Configuration**

#### Enable Coverage
```kotlin
// In shared/build.gradle.kts
plugins {
    id("org.jetbrains.kotlinx.kover")
}

kover {
    reports {
        html {
            setEnabled(true)
        }
        xml {
            setEnabled(true)
        }
    }
}
```

#### Run Coverage
```bash
# Generate coverage report
./gradlew :shared:koverHtmlReport

# View coverage in browser
open shared/build/reports/kover/html/index.html
```

### **Coverage Goals**

- **Minimum coverage**: 90% for shared module
- **Critical paths**: 100% coverage
- **Platform-specific**: 80% minimum

### **Coverage Exclusions**

```kotlin
kover {
    filters {
        classes {
            excludes += listOf(
                "com.yourcompany.yourapp.generated.*",
                "*MapperImpl",
                "*ComponentImpl"
            )
        }
    }
}
```

## 📚 Best Practices

### **1. Test Organization**
- **Group related tests** in the same test class
- **Use descriptive test names** that explain the scenario
- **Follow AAA pattern**: Arrange, Act, Assert
- **Keep tests focused** on single responsibility

### **2. Test Data Management**
- **Use test factories** for creating test objects
- **Create realistic test data** that represents real scenarios
- **Avoid hardcoded values** in test logic
- **Use builders** for complex object construction

### **3. Mocking Strategy**
- **Mock external dependencies** (APIs, databases)
- **Don't mock simple data objects** or value classes
- **Verify important interactions** with mocks
- **Use appropriate mock types** (mockk, relaxed, etc.)

### **4. Test Performance**
- **Run tests in parallel** when possible
- **Avoid slow operations** in unit tests
- **Use test doubles** for external services
- **Cache test data** when appropriate

### **5. Platform-Specific Testing**
- **Test expect/actual implementations** separately
- **Verify platform behavior** matches expectations
- **Use platform-specific test utilities** when needed
- **Maintain consistent test structure** across platforms

## 🐛 Troubleshooting

### **Common Issues**

#### 1. **Tests Not Running**
```bash
# Check if tests are discovered
./gradlew :shared:test --info

# Verify test source sets
./gradlew :shared:sourceSets
```

#### 2. **Mock Issues**
```bash
# Check mock configuration
./gradlew :shared:test --debug

# Verify mock setup
every { mock.method() } returns result
```

#### 3. **Coroutine Test Issues**
```bash
# Use runTest for coroutine tests
runTest {
    // Test coroutine code here
}

# Check dispatcher configuration
Dispatchers.setMain(Dispatchers.Unconfined)
```

### **Debug Mode**

#### Enable Verbose Logging
```bash
# Run with debug information
./gradlew :shared:test --debug

# Check test discovery
./gradlew :shared:test --scan
```

#### Test Isolation
```bash
# Run single test
./gradlew :shared:test --tests "*specific test name*"

# Run with specific JVM options
./gradlew :shared:test -Dkotest.framework.parallelism=1
```

## 🔗 Related Documentation

- [Code Coverage Reports](CODE_COVERAGE_REPORTS.md) - Coverage configuration and reporting
- [GitHub Actions Workflows](GITHUB_ACTIONS.md) - CI/CD test execution
- [Kotlin Format & Lint](KOTLIN_FORMAT_LINT.md) - Code quality tools
- [Pre-Commit Hooks](PRE_COMMIT_HOOKS.md) - Local test automation

## 📖 Resources

- [Kotest Documentation](https://kotest.io/)
- [Mokkery Documentation](https://github.com/mockk/mokkery)
- [Kotlin Testing Guide](https://kotlinlang.org/docs/testing.html)
- [Kover Coverage Tool](https://github.com/Kotlin/kotlinx-kover)

---

**Write comprehensive tests for reliable code! 🚀**