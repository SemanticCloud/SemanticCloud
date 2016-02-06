FROM java:8
ENV VERSION 0.1-SNAPSHOT
VOLUME /tmp
ADD dashboard-${VERSION}.war app.war
RUN bash -c 'touch /app.war'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.war"]
