FROM java:8
ENV VERSION 0.1-SNAPSHOT
VOLUME /tmp
ADD semantic-engine-${VERSION}.jar app.jar
ADD cloud.owl /opt/SemanticCloud/cloud.owl
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]