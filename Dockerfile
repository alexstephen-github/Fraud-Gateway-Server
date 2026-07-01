# ---- Build stage ----
FROM registry.access.redhat.com/ubi9/openjdk-21:latest AS build
WORKDIR /build

# Cache dependencies first
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Build the application
COPY src ./src
RUN mvn -B -q -DskipTests clean package

# ---- Runtime stage ----
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:latest
WORKDIR /deployments

# UBI OpenJDK images run as non-root user 185 by default, and tolerate an
# arbitrary UID assigned by OpenShift (all files owned by group root / GID 0).
COPY --chown=185:0 --from=build /build/target/fraud-gateway-server.jar /deployments/app.jar
RUN chmod -R g+rw /deployments

EXPOSE 8080

ENV JAVA_OPTS_APPEND="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# The UBI runtime image provides an entrypoint that launches the jar via
# JAVA_APP_JAR; we set it explicitly for clarity.
ENV JAVA_APP_JAR=/deployments/app.jar

# Fallback entrypoint if the base run script is unavailable.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS_APPEND -jar /deployments/app.jar"]
