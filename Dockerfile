
FROM openjdk:21
WORKDIR /app


COPY target/billingSystem-0.0.1-SNAPSHOT.jar /app/billingSystem-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "billingSystem-0.0.1-SNAPSHOT.jar"]
