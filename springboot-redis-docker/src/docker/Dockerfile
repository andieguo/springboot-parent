FROM java:8
VOLUME /tmp
ADD springboot-redis-docker-1.0.0.jar springboot-redis-docker-1.0.0.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=redis","/springboot-redis-docker-1.0.0.jar"]
EXPOSE 8082
