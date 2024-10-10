# Use a base image with Maven and OpenJDK 11
FROM maven:3.8.4-openjdk-11-slim AS build

# Set the working directory inside the container
WORKDIR /opt

# Copy the project's pom.xml to the container
COPY pom.xml .