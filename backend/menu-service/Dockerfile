FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

# Copy maven files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Build application
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Production stage
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp

# Copy dependency layers
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Create directory for image storage
RUN mkdir -p /app/images

ENTRYPOINT ["java","-cp","app:app/lib/*","com.quisin.menuservice.MenuServiceApplication"] 