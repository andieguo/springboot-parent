FROM maven:3.6.3-jdk-8 AS builder
COPY settings-docker.xml /usr/share/maven/ref/
WORKDIR /workdir/server
COPY pom.xml /workdir/server/pom.xml
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:go-offline

COPY src /workdir/server/src
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml install
RUN mkdir  -p target/depency
WORKDIR /workdir/server/target/dependency
RUN jar -xf ../*.jar

FROM openjdk:8

EXPOSE 8080
VOLUME /tmp
ARG DEPENDENCY=/workdir/server/target/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.andieguo.springboot.SpringBootWebApplication"]