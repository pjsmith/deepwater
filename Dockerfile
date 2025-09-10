# Base build stage
FROM clojure:tools-deps-1.12.2.1565-trixie-slim AS build-base

WORKDIR /app

# Copy project files (excluding client for server-only build)
COPY engine/ ./engine/
COPY server/ ./server/
COPY tools/ ./tools/

ARG GIT_SHA
RUN mkdir -p server/resources/public && echo ${GIT_SHA:-"unknown"} > server/resources/public/version.txt

# Server-only build stage
FROM build-base AS build-server

# Create the server-only uberjar
RUN cd server && clojure -P
RUN cd server && clojure -T:build uber

# Combined build stage (server + client resources)
FROM build-base AS build-combined

# Copy client files into server resources
COPY client/ ./server/resources/public/

# Create the combined uberjar with client resources included
RUN cd server && clojure -P
RUN cd server && clojure -T:build uber

# Server-only runtime
FROM openjdk:11-jre-slim AS server

WORKDIR /app

# Copy the server-only uberjar
COPY --from=build-server /app/server/target/deepwater.jar .

EXPOSE 3000
CMD ["java", "-jar", "deepwater.jar"]

# Combined runtime (default)
FROM openjdk:11-jre-slim AS combined

WORKDIR /app

# Copy the combined uberjar with client resources
COPY --from=build-combined /app/server/target/deepwater.jar .

EXPOSE 3000
CMD ["java", "-jar", "deepwater.jar"]

# Default target is combined
FROM combined
