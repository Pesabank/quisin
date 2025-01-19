FROM maven:3.9.9-eclipse-temurin-17-alpine as build
WORKDIR /build

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built artifact
COPY --from=build /build/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"] 