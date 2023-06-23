FROM maven:3.8-openjdk-18-slim AS build
LABEL maintainer="wmconstructor@gmail.com"
WORKDIR /build
COPY src /build/src
COPY pom.xml .
RUN mvn dependency:go-offline -B
RUN mvn package -DskipTests

FROM tomcat:10-jdk17-temurin
LABEL maintainer="imperial-union"
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=build /build/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080

CMD ["catalina.sh", "run"]