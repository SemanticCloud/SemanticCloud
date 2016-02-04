FROM java:8
ENV VERSION 0.1-SNAPSHOT
VOLUME /tmp
ADD config-service-${VERSION}.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 8888
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]