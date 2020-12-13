FROM java:8
VOLUME /tmp
ADD springboot-mysql-docker-2.0.0.jar springboot-mysql-docker-2.0.0.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","/springboot-mysql-docker-2.0.0.jar"]
EXPOSE 8082
