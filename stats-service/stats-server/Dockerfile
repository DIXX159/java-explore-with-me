FROM amazoncorretto:11-alpine-jdk
VOLUME /tmp
COPY target/*.jar main-server.jar
ENTRYPOINT ["java","-jar","/main-server.jar"]