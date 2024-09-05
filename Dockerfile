FROM openjdk:17-oracle
COPY /target/CFB-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]