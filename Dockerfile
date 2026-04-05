# Use official Java image
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Build the project
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Run the jar file
CMD ["java", "-jar", "target/*.jar"]