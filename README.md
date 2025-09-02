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

## How to Run the application

### SpringBoot approach
- Pre-requisites
  - Java 21
- Run the following commands:
  1. Run Tests and Build Jar file. (Optional)
     - `./gradlew clean build`
  2. Run the SpringBoot application.
     - `./gradlew bootRun`

### Docker Container
#### Missing Local Setup (No Jar Locally)
- Pre-requisites
  - Docker
- Run the following commands:
  1. Creates the Jar file within an ephemeral container and outputs a distro-less container with the application Jar.
     - `docker build --tag user-management-api --file Dockerfile.no-local .`
  2. Runs the distro-less image and exposes ports application to the host machine's port 8080.
     - `docker run --publish 8080:8080 --name user-management-api --detach user-management-api`

#### With Local Setup (Uses Local Jar)
- Pre-requisites
    - Docker
    - Java 21
- Run the following commands:
  1. Build your Jar locally
     - `./gradlew clean assemble`
  2. Copies the jar file and outputs a distro-less container with the application Jar.
     - `docker build --tag user-management-api .`
  3. Runs the distro-less image and exposes ports application to the host machine's port 8080.
     - `docker run --publish 8080:8080 --name user-management-api --detach user-management-api`

#### Cleaning local Container and Images
- To remove and clean up, run the following commands:
  1. Stops and removes running container of `user-management-api`:
      - `docker container rm --force user-management-api`
  2. Removes docker image of `user-management-api`:
      - `docker image rm --force user-management-api`

## How to Interact with Endpoints of application
- You can interact with endpoints the built-in swagger ui at the following link: http://localhost:8080/swagger-ui/index.html
- Alternatively, you can use the terminal or your preferred API Tool.
