FROM amazoncorretto:11
MAINTAINER rstzkrt
COPY . .
COPY target/tattoo-artist-backend-0.0.1-SNAPSHOT.jar backend-1.0.1.jar
ENTRYPOINT ["java","-jar","/backend-1.0.1.jar"]