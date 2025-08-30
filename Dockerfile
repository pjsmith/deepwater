# Build stage
FROM clojure:tools-deps-1.11.1.1413 AS build

WORKDIR /app

# Copy all project files
COPY . .

# Create the uberjar
RUN cd server && clojure -P
RUN clojure -T:build uber

# Runtime stage
FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the uberjar from the build stage
COPY --from=build /app/server/target/deepwater.jar .

# Expose the port the app runs on
EXPOSE 3000

# Run the application
CMD ["java", "-jar", "deepwater.jar"]
