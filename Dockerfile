FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine

ENV APP_HOME /usr/app

WORKDIR $APP_HOME

COPY target/fileupload-0.0.1-SNAPSHOT.jar .

COPY ./upload-dir $APP_HOME

VOLUME ["/usr/app/upload-dir"]

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar fileupload-0.0.1-SNAPSHOT.jar"]
