FROM maven:3.9.6-eclipse-temurin-17 AS build
# Set the working directory inside the container
WORKDIR /app
# Copy the Maven project files (pom.xml and source code)
COPY pom.xml .
# RUN mvn dependency:go-offline
# Copy the source code to the container
COPY src ./src
# Build the project
RUN mvn verify -DskipTests

# Use a lightweight JDK image for running the application
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/berlinapple-*.jar /app/berlinapple.jar
ADD ./src/docker/entrypoint.sh /app/entrypoint.sh

RUN set -ex && chmod 755 /app/entrypoint.sh /app/berlinapple.jar && mkdir -p /app/config /app/logs

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
