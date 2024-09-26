#!/bin/bash

# Load environment variables from the .env file
if [ -f .env ]; then
  echo "Loading environment variables from .env file..."
  source .env
else
  echo ".env file not found!"
  exit 1
fi

# Check if all required environment variables are set
if [[ -z "$DB_USERNAME" || -z "$DB_PASSWORD" || -z "$TLGBOT_WEBHOOK_URL" || -z "$TLGBOT_BOT_TOKEN" ]]; then
  echo "Missing required environment variables!"
  exit 1
fi

# Build the project using Maven
echo "Running 'mvn clean install'..."
if ! mvn clean install; then
  echo "Maven build failed!"
  exit 1
fi

# Find the JAR file dynamically
JAR_FILE=$(find target -type f -name "*.jar" -print -quit)

if [ -z "$JAR_FILE" ]; then
  echo "JAR file not found in the target directory!"
  exit 1
fi

# Run the Spring Boot application
echo "Running the Spring Boot application: $JAR_FILE"
if ! java -jar "$JAR_FILE" --spring.profiles.active=prod; then
  echo "Failed to run the Spring Boot application!"
  exit 1
fi