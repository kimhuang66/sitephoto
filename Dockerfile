ARG   BUILDER_IMAGE=maven
ARG   BUILDER_VERSION=3.5.2-jdk-8-alpine
ARG   BASE_IMAGE=java
ARG   BASE_VERSION=8-jre-alpine

FROM ${BUILDER_IMAGE}:${BUILDER_VERSION} AS builder
LABEL maintainer="OIT Packaging Adoption & Customization, kimhuang@princeton.edu"
LABEL stage="build"
COPY pom.xml /tmp/
RUN mvn -B dependency:go-offline -f /tmp/pom.xml -s /usr/share/maven/ref/settings-docker.xml
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml package

#FROM java:8-jre-alpine
FROM ${BASE_IMAGE}:${BASE_VERSION}
LABEL maintainer="OIT Packaging Adoption & Customization, kimhuang@princeton.edu"
LABEL stage="final"
EXPOSE 8080

RUN mkdir /app
COPY --from=builder /tmp/target/*.jar /app/sitephotomvn.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dsitephotoltiprovider.properties=file:/app/sitephotoltiprovider.properties","-jar","/app/sitephotomvn.jar","--spring.config.location=/app/sitephotoltiprovider.properties"]