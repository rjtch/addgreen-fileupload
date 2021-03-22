FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine

ENV APP_HOME /usr/app
WORKDIR $APP_HOME

COPY target/*.jar .

VOLUME ["/usr/app/upload-dir"]

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar fileupload-*.jar"]
