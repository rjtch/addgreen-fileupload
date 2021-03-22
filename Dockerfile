FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY /target/artifact/fileupload-0.0.1-SNAPSHOT.jar .

VOLUME ["/usr/app/upload-dir"]

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar fileupload-0.0.1-SNAPSHOT.jar"]
