FROM openjdk:8u275-slim

WORKDIR /
ADD excel.jar app.jar
RUN mkdir chunk upload
EXPOSE 8080

ENTRYPOINT ["nohup", "java", "-jar", "-server", "-Xms1024m", "-Xmx1024m", "app.jar", "&"]