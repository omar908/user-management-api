FROM eclipse-temurin:21.0.8_9-jdk AS java21
WORKDIR /app
COPY ./build/libs/user-management-api-0.0.1-SNAPSHOT.jar ./user-management-api.jar
EXPOSE 8080
CMD ["java", "-jar", "user-management-api.jar"]

