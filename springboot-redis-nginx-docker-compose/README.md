## springboot-redis-nginx-docker-compose
### 工程结构

本工程是一个通过docker + compose 编译运行springboot + redis 应用，同时通过nginx实现对webapp访问的负载均衡；

```
.
├── nginx
│   ├── nginx.conf
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

compose file定义了5个services：包括3个webapp，1个redis, 1个nginx

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

  master:
    container_name: master
    image: nginx
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
    ports:
      - "8089:80"
    depends_on:
      - webapp1
      - webapp2
      - webapp3

```

## 编译docker-compose

> tips:当你改变本地代码之后，先执行docker-compose build构建新的镜像，然后执行docker-compose up -d基于最新build镜像启动容器；

``` 
guodong@mars springboot-redis-nginx-docker-compose % docker-compose build         
redis uses an image, skipping
master uses an image, skipping
Building webapp3
Step 1/18 : FROM maven:3.6.3-jdk-8 AS builder
 ---> 8c0e3cc0db61
Step 2/18 : COPY settings-docker.xml /usr/share/maven/ref/
 ---> Using cache

...
Step 7/18 : RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml install
 ---> Running in 515c321a3ff3
[INFO] Scanning for projects...
[INFO] 
[INFO] ------< org.springframework.boot:webapp-compose >------
[INFO] Building webapp-compose 1.0.0

Successfully built edca08cf27e1
Successfully tagged springboot-redis-nginx-docker-compose_webapp3:latest
Building webapp2

Successfully built edca08cf27e1
Successfully tagged springboot-redis-nginx-docker-compose_webapp2:latest
Building webapp1

Step 18/18 : ENTRYPOINT ["java","-cp","app:app/lib/*","com.andieguo.springboot.SpringBootWebApplication"]
 ---> Using cache
 ---> edca08cf27e1

Successfully built edca08cf27e1
Successfully tagged springboot-redis-nginx-docker-compose_webapp1:latest
```


## 部署docker-compose

```
$ docker-compose up -d
guodong@mars springboot-redis-nginx-docker-compose % docker-compose up -d
Creating network "springboot-redis-nginx-docker-compose_default" with the default driver
Creating redis ... done
Creating webapp-8084 ... done
Creating webapp-8083 ... done
Creating webapp-8085 ... done
Creating master                       ... done

```

## 运行结果

- 查看运行的容器和端口号
```
guodong@mars springboot-redis-nginx-docker-compose % docker ps
CONTAINER ID        IMAGE                                           COMMAND                  CREATED             STATUS              PORTS                              NAMES
c5d84b134dfb        nginx                                           "/docker-entrypoint.…"   33 seconds ago      Up 32 seconds       0.0.0.0:8089->80/tcp               master
3802014a26f2        springboot-redis-nginx-docker-compose_webapp3   "java -cp app:app/li…"   34 seconds ago      Up 33 seconds       8080/tcp, 0.0.0.0:8085->8082/tcp   webapp-8085
3b3fc66a4122        springboot-redis-nginx-docker-compose_webapp2   "java -cp app:app/li…"   34 seconds ago      Up 33 seconds       8080/tcp, 0.0.0.0:8084->8082/tcp   webapp-8084
d3c74ea6fa72        springboot-redis-nginx-docker-compose_webapp1   "java -cp app:app/li…"   34 seconds ago      Up 33 seconds       8080/tcp, 0.0.0.0:8083->8082/tcp   webapp-8083
c8271924d7b2        redis:latest                                    "docker-entrypoint.s…"   35 seconds ago      Up 34 seconds       0.0.0.0:6379->6379/tcp             redis
```

- webapp、redis、nginx启动后, 访问nginx服务器：`http://localhost:8089` :
```
 ago      Up 34 seconds       0.0.0.0:6379->6379/tcp             redis
guodong@mars springboot-redis-nginx-docker-compose % curl localhost:8089
the current page ipAddress is 192.168.176.4
the current page has been accessed 1 times                                                         
guodong@mars springboot-redis-nginx-docker-compose % curl localhost:8089
the current page ipAddress is 192.168.176.5
the current page has been accessed 2 times                                                         
guodong@mars springboot-redis-nginx-docker-compose % curl localhost:8089
the current page ipAddress is 192.168.176.3
the current page has been accessed 3 times                                                          
guodong@mars springboot-redis-nginx-docker-compose % curl localhost:8089
the current page ipAddress is 192.168.176.4
the current page has been accessed 4 times                                                         
```

- 停止并且移除容器
```
guodong@mars springboot-redis-nginx-docker-compose % docker-compose down 
Stopping master                       ... done
Stopping webapp-8085 ... done
Stopping webapp-8084 ... done
Stopping webapp-8083 ... done
Stopping redis                        ... done
Removing master                       ... done
Removing webapp-8085 ... done
Removing webapp-8084 ... done
Removing webapp-8083 ... done
Removing redis                        ... done
Removing network springboot-redis-nginx-docker-compose_default

```
