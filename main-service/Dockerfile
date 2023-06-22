FROM amazoncorretto:11-alpine-jdk
VOLUME /tmp
COPY target/*.jar main-service.jar
ENTRYPOINT ["java","-jar","/main-service.jar"]