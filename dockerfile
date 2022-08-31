FROM maven:3.8.3-jdk-11-slim AS build

WORKDIR /usr/local/src/OT-server
COPY ./ /usr/local/src/OT-server

RUN mvn clean package

COPY ./target/ /usr/local/src/OT-server/target/

EXPOSE 8443
ENTRYPOINT ["java","-jar","target/OT-server-1.0.0.war","--spring.profiles.active=prod"]
