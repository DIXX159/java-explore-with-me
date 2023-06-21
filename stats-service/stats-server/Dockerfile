FROM amazoncorretto:11-alpine-jdk
#VOLUME /tmp
#ARG JAR_FILE=target/*.jar
COPY target/*.jar stats-server.jar
ENTRYPOINT ["java"," -jar","stats-server.jar"]