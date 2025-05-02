#
# Build stage
#
FROM maven:3.9.5 AS builder
WORKDIR /home/app

COPY target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

#
# Run stage
#
FROM openjdk:21-jdk-slim

# RUN apt update
# RUN apt install curl -y

WORKDIR /app

COPY --from=builder /home/app/dependencies/ ./
COPY --from=builder /home/app/snapshot-dependencies/ ./
COPY --from=builder /home/app/spring-boot-loader/ ./
COPY --from=builder /home/app/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
