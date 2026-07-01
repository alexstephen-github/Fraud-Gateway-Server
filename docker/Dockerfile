# Stage 1: Build the application
FROM registry.access.redhat.com/ubi9/openjdk-21 AS builder

USER root
WORKDIR /app

# Copy pom.xml and sources, then build.
# Build context is the repository root (see devfile.yaml / BuildConfig).
COPY pom.xml .
COPY src ./src

# Build the application (skip tests for a faster image build)
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime

ENV LANGUAGE='en_US:en'

# Copy the built artifact from the builder stage.
# UBI OpenJDK images run as non-root (UID 185) and tolerate an arbitrary UID
# assigned by OpenShift when files are group-owned by root (GID 0).
COPY --chown=185:0 --from=builder /app/target/fraud-gateway-server.jar /deployments/app.jar
RUN chmod -R g+rw /deployments

# Expose the application port
EXPOSE 8080

ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
ENV JAVA_OPTS_APPEND="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV JAVA_APP_JAR="/deployments/app.jar"

ENTRYPOINT ["java", "-jar", "/deployments/app.jar"]
