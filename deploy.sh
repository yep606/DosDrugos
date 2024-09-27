#!/bin/bash

# Load environment variables from the .env file
if [ -f .env ]; then
  echo "Loading environment variables from .env file..."
  export $(grep -v '^#' .env | xargs)  # Export all variables in the .env file
else
  echo ".env file not found!"
  exit 1
fi

# Check if all required environment variables are set
if [[ -z "$DB_USERNAME" || -z "$DB_PASSWORD" || -z "$WEBHOOK_URL" || -z "$BOT_TOKEN" ]]; then
  echo "Missing required environment variables!"
  exit 1
fi

# Build the project using Maven with environment variables passed as Maven properties
echo "Running 'mvn clean install' with environment variables..."
if ! mvn clean install \
  -Dspring.profiles.active=prod;
    then
  echo "Maven build failed!"
  exit 1
fi

# Find the JAR file dynamically
JAR_FILE=$(find target -type f -name "*.jar" -print -quit)

if [ -z "$JAR_FILE" ]; then
  echo "JAR file not found in the target directory!"
  exit 1
fi

# Run the Spring Boot application with the active profile and pass environment variables
echo "Running the Spring Boot application: $JAR_FILE"
java -Dspring.profiles.active=prod \
  -jar "$JAR_FILE"