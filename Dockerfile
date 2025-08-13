FROM clojure:latest AS builder
WORKDIR /app
COPY . .
RUN clj -T:build uberjar

FROM openjdk:17-jre-slim-buster
WORKDIR /app
COPY --from=builder /app/target/deepwater-standalone.jar .
CMD ["java", "-jar", "deepwater-standalone.jar"]
