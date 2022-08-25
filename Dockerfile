FROM amazoncorretto:17-alpine
MAINTAINER rstzkrt
COPY src/main/resources/firebase/firebase_config.json /tmp
ENV GOOGLE_APPLICATION_CREDENTIALS=tmp/firebase_config.json
COPY target/tattoo-artist-backend-0.0.1-SNAPSHOT.jar backend-1.0.1.jar
ENTRYPOINT ["java","-jar","/backend-1.0.1.jar"]