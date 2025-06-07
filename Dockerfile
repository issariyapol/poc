# Use the official OpenJDK image
FROM openjdk:11-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the project files
COPY . /app

# Package the application (assumes Maven Wrapper is included)
RUN ./mvnw package

# Run the application
CMD "target/demo-0.0.1-SNAPSHOT.jar"]
