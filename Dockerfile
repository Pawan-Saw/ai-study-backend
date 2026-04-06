FROM eclipse-temurin:21-jdk

WORKDIR /app

# Pehle dependencies copy karo (caching ke liye)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Source code copy karo
COPY src src

# Build karo
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# JAR run karo
EXPOSE 8080
CMD ["sh", "-c", "java -jar target/*.jar"]