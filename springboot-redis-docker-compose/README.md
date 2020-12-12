## springboot-redis-docker-compose
### 工程结构

本工程是一个通过docker + compose 编译运行springboot + redis 应用的demo

```
.
├── webapp
│   ├── src
│   ├── Dockerfile
│   └── pom.xml
│   └── settings-docker.xml
├── redis
├── docker-compose.yml
└── README.md

```

[_docker-compose.yml_](docker-compose.yml)

compose file定义了4个services，3个webapp，1个nginx

```
version: "3"

services:
  webapp1:
    container_name: springboot-redis-docker-8083
    build: webapp
    ports:
      - "8083:8082"
    depends_on:
      - redis

  ...
  redis:
    container_name: redis
    image: redis:latest
    restart: always
    ports:
      - "6379:6379"


```

## 部署docker-compose

```
$ docker-compose up -d
Building webapp2
Step 1/18 : FROM maven:3.6.3-jdk-8 AS builder
 ---> 8c0e3cc0db61
Step 2/18 : COPY settings-docker.xml /usr/share/maven/ref/
 ---> Using cache
 ---> 80e0e2b80193
Step 3/18 : WORKDIR /workdir/server
 ---> Using cache
 ---> 0cf23c8e075e
Step 4/18 : COPY pom.xml /workdir/server/pom.xml
 ---> Using cache
 ---> 28b312ea346c
Step 5/18 : RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:go-offline
 ---> Using cache
 ---> e2cd150cd919
Step 6/18 : COPY src /workdir/server/src
 ---> Using cache
 ---> de727342d9af
Step 7/18 : RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml install
 ---> Using cache
 ---> 0f02d3d863b0
Step 8/18 : RUN mkdir  -p target/depency
 ---> Using cache
 ---> de3e5fd3ddf5
Step 9/18 : WORKDIR /workdir/server/target/dependency
 ---> Using cache
 ---> 958a0d44825f
Step 10/18 : RUN jar -xf ../*.jar
 ---> Using cache
 ---> d5e008b65a42

Step 11/18 : FROM openjdk:8
 ---> 82f24ce79de6
Step 12/18 : EXPOSE 8080
 ---> Using cache
 ---> f8eba69bb3af
Step 13/18 : VOLUME /tmp
 ---> Using cache
 ---> 266855e4db6b
Step 14/18 : ARG DEPENDENCY=/workdir/server/target/dependency
 ---> Using cache
 ---> b5f7ecd6d81d
Step 15/18 : COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
 ---> Using cache
 ---> e8fe1754bdee
Step 16/18 : COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
 ---> Using cache
 ---> 30a9ec381111
Step 17/18 : COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
 ---> Using cache
 ---> 3f7bee36c53e
Step 18/18 : ENTRYPOINT ["java","-cp","app:app/lib/*","com.andieguo.springboot.SpringBootWebApplication"]
 ---> Using cache
 ---> 6f0b11d63cc5

Successfully built 6f0b11d63cc5
Successfully tagged springboot-redis-docker-compose_webapp2:latest

WARNING: Image for service webapp1 was built because it did not already exist. To rebuild this image you must use `docker-compose build` or `docker-compose up --build`.
Creating redis ... done
Creating springboot-redis-docker-8084 ... done
Creating springboot-redis-docker-8083 ... done
Creating springboot-redis-docker-8085 ... done
```

## 运行结果

查看运行的容器和端口号
```
guodong@mars gitworkspace % docker ps
CONTAINER ID        IMAGE                                     COMMAND                  CREATED             STATUS              PORTS                              NAMES
0fef56883503        springboot-redis-docker-compose_webapp1   "java -cp app:app/li…"   16 minutes ago      Up 16 minutes       8080/tcp, 0.0.0.0:8083->8082/tcp   springboot-redis-docker-8083
36a5bc4775c2        springboot-redis-docker-compose_webapp2   "java -cp app:app/li…"   16 minutes ago      Up 16 minutes       8080/tcp, 0.0.0.0:8084->8082/tcp   springboot-redis-docker-8084
9fa85ec29299        springboot-redis-docker-compose_webapp3   "java -cp app:app/li…"   16 minutes ago      Up 16 minutes       8080/tcp, 0.0.0.0:8085->8082/tcp   springboot-redis-docker-8085
3231b4d7dd3a        redis:latest                              "docker-entrypoint.s…"   16 minutes ago      Up 16 minutes       0.0.0.0:6379->6379/tcp             redis
```

webapp应用程序和nginx启动后, 访问：`http://localhost:8083` :
```
guodong@mars gitworkspace % curl localhost:8083
That's one small step for man,one giant leap for mankind,21%                                                                                 
guodong@mars gitworkspace % curl localhost:8084
That's one small step for man,one giant leap for mankind,22%                                                                                 
guodong@mars gitworkspace % curl localhost:8085
That's one small step for man,one giant leap for mankind,23%                                                                                 
guodong@mars gitworkspace % curl localhost:8085
That's one small step for man,one giant leap for mankind,24%                                                                                 
guodong@mars gitworkspace % curl localhost:8085
That's one small step for man,one giant leap for mankind,25%                                                                                 
guodong@mars gitworkspace % curl localhost:8083
That's one small step for man,one giant leap for mankind,26%                                                                                 
```

停止并且移除容器
```
guodong@mars springboot-redis-docker-compose % docker-compose down
Stopping springboot-redis-docker-8083 ... done
Stopping springboot-redis-docker-8084 ... done
Stopping springboot-redis-docker-8085 ... done
Stopping redis                        ... done
Removing springboot-redis-docker-8083 ... done
Removing springboot-redis-docker-8084 ... done
Removing springboot-redis-docker-8085 ... done
Removing redis                        ... done
Removing network springboot-redis-docker-compose_default
guodong@mars spri
```
