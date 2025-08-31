# ReadMe
## Description
- The User Management API is a Spring Boot application designed to showcase clean, scalable architecture and best practices for building production-ready services.
- It highlights RESTful API design, testing practices, and externalized configuration. 
- By default, it uses an in-memory store, but the design makes it easy to plug in a real database by implementing the `UserRepository` interface.

## Features
- Profile Management (view and update user details)
- Configuration Management (externalized settings with Spring Boot properties)
- Designed with an API-first approach and adhering to RESTful best practices.

## Testing Approach
- Unit Tests: for isolated business logic
- Slice Tests: for Spring components like controllers or repositories
- Integration Tests: for verifying multiple layers work together
- Smoke Test (contextLoads()): ensures the application context boots successfully

## Tech Stack
- Java 21
- Spring Boot 3.x
- Spring Web / Spring Actuator
- **In-memory repository** (pure Java collections, resets on restart)
- JUnit 5, Mockito, Data Faker, & Spring Test

## How to Run
### Pre-requisites
  - Java 21

### Running the application
- Run the following commands:
  - `./gradlew clean build`
  - `./gradlew bootRun`

### Interacting with Endpoints
- You can interact with endpoints the built-in swagger ui at the following link: http://localhost:8080/swagger-ui/index.html
- Alternatively, you can use the terminal or your preferred API Tool.
