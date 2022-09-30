FROM amazoncorretto:17-alpine
MAINTAINER rstzkrt
COPY . .
ENV GOOGLE_APPLICATION_CREDENTIALS=src/resources/firebase/firebase_config.json
COPY target/tattoo-artist-backend-0.0.1-SNAPSHOT.jar backend-1.0.1.jar
ENTRYPOINT ["java","-jar","/backend-1.0.1.jar"]