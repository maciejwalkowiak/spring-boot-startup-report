# Spring Boot Startup Report

**Spring Boot Startup Report** library generates an interactive Spring Boot application startup report that lets you understand 
what contributes to the application startup time and perhaps helps to optimize it.

> **Warning**
> Project is early stage and is no perfect by any means. Please suggest any improvement you have in mind and PRs are more than welcome! 

## 🤩 Highlights

- startup report available in runtime as an interactive HTML page
- generating startup reports in integration tests
- flame chart
- search by class or an annotation

Report table lets you dig into the details of beans instantiation:

![Report](docs/images/table.png)

Flame graphs is a more visual representation of the data from the table:

![Report](docs/images/flamechart.png)

## ✨ How to use

1. Add the dependency to `spring-boot-startup-report`:

```xml
<dependency>
    <groupId>com.maciejwalkowiak.spring</groupId>
    <artifactId>spring-boot-startup-report</artifactId>
    <version>1.0.0</version>
</dependency>
```

2. Run application.

3. Assuming application runs on port `8080`, go to `http://localhost:8080/startup-report`

> **Note**
> This library has dependencies to `org.springframework:spring-test` and `org.springframework.boot:spring-boot-test` so most likely you don't want to include it to your production build and it makes more sense to keep in in the test scope

## Using with integration tests

When library is on the classpath, it also automatically generates startup reports for each application context started during running integration tests (tests annotated with `@SpringBootTest`). 
You'll find them in `target/startup-reports` for Maven projects and in `build/startup-reports` for Gradle.

For integration tests that do not use `@SpringBootTest` but `@WebMvcTest`, `@DataJpaTest` or any other test slice, add `@Import(StartupEventsAutoConfiguration.class)` on the top of the test class to enable generating report.

```java
@Import(StartupEventsAutoConfiguration.class)
@WebMvcTest(OwnerController.class)
public class OwnerControllerTests {
    ...
}
```

If you need only to generate reports for tests, but do not need to have the report available in runtime under an endpoint, you can declare the dependency with a `test` scope:

```xml
<dependency>
    <groupId>com.maciejwalkowiak.spring</groupId>
    <artifactId>spring-boot-startup-report</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

Sounds good? Consider [❤️ Sponsoring](https://github.com/sponsors/maciejwalkowiak) the project! Thank you!
