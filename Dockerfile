# Use official Java image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Build the project
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Run the jar file
CMD ["java", "-jar", "target/*.jar"]