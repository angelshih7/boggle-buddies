# Docker react: uncomment once frontend has react files in. 
FROM node:20.11.1-slim AS frontend
WORKDIR /frontend
# must change path to where React app is located
COPY frontend/package*.json ./
RUN npm ci
EXPOSE 3000
COPY frontend/ ./ 

#RUN npm run build


FROM eclipse-temurin:21-jdk AS build 
WORKDIR /app

RUN apt update && apt install -y findutils

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src ./src

# react copies react files into resources so that a react app structure exist o
# dockerfile run 
RUN rm -rf /BoggleSprintBoot/src/main/resources/static && mkdir -p src/main/resources/static
COPY --from=frontend /frontend/build/ /BoggleSprintBoot/src/main/resources/static/

# Fix for Windows users
RUN apt-get update && apt-get install -y dos2unix \
   && dos2unix gradlew \
   && chmod +x gradlew

# Build the JAR
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

# Fix for Windows users
RUN apt-get update && apt-get install -y dos2unix

# Copy built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Default: run the JAR
CMD ["java", "-jar", "app.jar"]
