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

compose file定义了4个services：包括3个webapp，1个redis

```
version: "3"

services:
  webapp1:
    container_name: webapp-8083
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
...
Step 18/18 : ENTRYPOINT ["java","-cp","app:app/lib/*","com.andieguo.springboot.SpringBootWebApplication"]
 ---> Using cache
 ---> 6f0b11d63cc5

Successfully built 6f0b11d63cc5
Successfully tagged webapp-compose_webapp2:latest

WARNING: Image for service webapp1 was built because it did not already exist. To rebuild this image you must use `docker-compose build` or `docker-compose up --build`.
Creating redis ... done
Creating webapp-8084 ... done
Creating webapp-8083 ... done
Creating webapp-8085 ... done
```

## 运行结果

- 查看运行的容器和端口号
```
% docker ps
CONTAINER ID        IMAGE                                     COMMAND                  CREATED             STATUS              PORTS                              NAMES
0fef56883503        webapp-compose_webapp1   "java -cp app:app/li…"   16 minutes ago      Up 16 minutes       8080/tcp, 0.0.0.0:8083->8082/tcp   webapp-8083
36a5bc4775c2        webapp-compose_webapp2   "java -cp app:app/li…"   16 minutes ago      Up 16 minutes       8080/tcp, 0.0.0.0:8084->8082/tcp   webapp-8084
9fa85ec29299        webapp-compose_webapp3   "java -cp app:app/li…"   16 minutes ago      Up 16 minutes       8080/tcp, 0.0.0.0:8085->8082/tcp   webapp-8085
3231b4d7dd3a        redis:latest                              "docker-entrypoint.s…"   16 minutes ago      Up 16 minutes       0.0.0.0:6379->6379/tcp             redis
```

- webapp应用程序、redis启动后, 通过不同的端口号访问webapp :
```
guodong@mars springboot-parent % curl localhost:8083
the current page ipAddress is 172.24.0.3
the current page has been accessed 1 times%                                                          
guodong@mars springboot-parent % curl localhost:8084
the current page ipAddress is 172.24.0.4
the current page has been accessed 2 times%                                                          
guodong@mars springboot-parent % curl localhost:8085
the current page ipAddress is 172.24.0.5
the current page has been accessed 3 times%                                                                                                                                          
```

- 停止并且移除容器
```
% docker-compose down
Stopping webapp-8083 ... done
Stopping webapp-8084 ... done
Stopping webapp-8085 ... done
Stopping redis                        ... done
Removing webapp-8083 ... done
Removing webapp-8084 ... done
Removing webapp-8085 ... done
Removing redis                        ... done
Removing network webapp-compose_default
```
