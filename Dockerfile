# Use a base image with Maven and OpenJDK 11
FROM maven:3.8.4-openjdk-11-slim AS build

# Set the working directory inside the container
WORKDIR /opt

# Copy the project's pom.xml to the container
COPY pom.xml .

# Download the dependencies and plugins needed for the build
RUN mvn dependency:go-offline

# Copy the rest of the project files to the container
COPY src src

# Build the application without running tests
RUN mvn package -DskipTests

# Use a base image with OpenJDK 11
FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-slim

# Set the working directory inside the container
WORKDIR /opt

# Define an environment variable for the port (optional)
ENV PORT 8080

# Expose the port the application will run on
EXPOSE 8080

# Copy the JAR file from the build stage to the container's filesystem
COPY --from=build /opt/target/*.jar /opt/app.jar

# Define the entry point for your application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]