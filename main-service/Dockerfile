FROM amazoncorretto:11-alpine-jdk
#VOLUME /tmp
#ARG JAR_FILE=target/*.jar
COPY target/*.jar main-service.jar
ENTRYPOINT ["java","-jar","/main-service.jar"]