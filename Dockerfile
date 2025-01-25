# Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
# Set the working directory inside the container
WORKDIR /app
# Copy the Maven project files (pom.xml and source code)
COPY pom.xml .
COPY src ./src
# Build the project
# RUN mvn dependency:go-offline
RUN mvn verify -DskipTests

# Run
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/berlinapple-*.jar /app/berlinapple.jar
RUN set -ex && chmod 755 /app/berlinapple.jar && mkdir -p /app/config /app/logs
EXPOSE 8080
CMD java -showversion -Xmx1G -jar /app/berlinapple.jar
