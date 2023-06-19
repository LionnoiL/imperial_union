FROM tomcat:10-jdk17-temurin
LABEL maintainer="imperial-union"

RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY target/imperialunion.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]