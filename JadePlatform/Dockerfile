FROM java:8
RUN wget http://jade.tilab.com/dl.php?file=JADE-bin-4.4.0.zip -O jade.zip
RUN unzip jade.zip -d /opt
RUN rm jade.zip
ADD jade.properties /opt/jade
CMD ["java", "-cp", "/opt/jade/lib/*", "jade.Boot", "-conf", "/opt/jade/jade.properties"]