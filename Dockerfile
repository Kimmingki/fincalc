FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/fincalc-0.0.1-SNAPSHOT.jar ./fincalc.jar

CMD ["java", "-jar", "fincalc.jar"]

EXPOSE 8088