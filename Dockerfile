FROM openjdk:18
COPY target/database.jar database.jar

ENTRYPOINT ["java","-jar","/database.jar"]

