## springboot-mysql-docker-compose
### 工程结构

本工程是一个通过docker + compose 编译运行springboot + mysql 应用的demo

```
.
├── webapp
│   ├── src
│   ├── Dockerfile
│   └── pom.xml
│   └── settings-docker.xml
├── mysql
├── docker-compose.yml
└── README.md

```

[_docker-compose.yml_](docker-compose.yml)

compose file定义了4个service：包括3个webapp，1个mysql
```
version: "3"

services:
  webapp1:
    container_name: springboot-mysql-8083
    build: webapp
    restart: on-failure
    ports:
      - "8083:8082"
    depends_on:
      - db

  ...

  db:
    container_name: mysql
    image: mysql:latest
    restart: always
    environment:
      MYSQL_DATABASE: example
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"

```

- 关于容器间网络通信：
> 当docker-compose 执行docker-compose.yml文件时会自动在容器间创建一个网络，每一个容器都能够立即通过名字来访问到另外的容器。 因此不再需要依赖links，links过去通常用来开始db容器和web容器网络之间的通讯，但是这一步已经被docker-compose实现了；
- 关于depends_on：
> 上面的docker-compose例子中，db容器启动顺序要优先于webapp容器，当启动webapp容器时会自动创建db容器。特别需要注意的是，depends_on不会保证等到db容器ready再启动webapp，webapp容器仅仅等到db容器启动就开始启动，因此会存在db容器还未完全启动完毕，webapp容器已开始启动，并尝试访问db容器，这时会报Connection refused 异常，解决办法：通过restart: on-failure配置webapp容器启动失败后自动重启

异常关键词：springboot mysql docker-compose Connection refused

```
Caused by: java.net.ConnectException: Connection refused (Connection refused)
at java.net.PlainSocketImpl.socketConnect(Native Method) ~[na:1.8.0_275]
at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350) ~[na:1.8.0_275]
at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206) ~[na:1.8.0_275]
at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188) ~[na:1.8.0_275]
at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392) ~[na:1.8.0_275]
at java.net.Socket.connect(Socket.java:607) ~[na:1.8.0_275]
at com.mysql.cj.protocol.StandardSocketFactory.connect(StandardSocketFactory.java:155) ~[mysql-connector-java-8.0.22.jar:8.0.22]
at com.mysql.cj.protocol.a.NativeSocketConnection.connect(NativeSocketConnection.java:63) ~[mysql-connector-java-8.0.22.jar:8.0.22]
```


## 部署docker-compose

- 编译docker-compose
```
guodong@mars springboot-mysql-docker-compose % docker-compose build
db uses an image, skipping
Building webapp3
Step 1/18 : FROM maven:3.6.3-jdk-8 AS builder
 ---> 8c0e3cc0db61
Step 2/18 : COPY settings-docker.xml /usr/share/maven/ref/
 ---> Using cache
 ---> 9399e362f00c
...

Successfully built 6f8c7f169f19
Successfully tagged springboot-mysql-docker-compose_webapp3:latest
Building webapp2
...
Successfully built 6f8c7f169f19
Successfully tagged springboot-mysql-docker-compose_webapp1:latest
guodong@mars springboot-mysql-docker-compose % 

```

- 运行docker-compose

```
guodong@mars springboot-mysql-docker-compose % docker-compose up
Creating network "springboot-mysql-docker-compose_default" with the default driver
Creating mysql ... done
Creating springboot-mysql-8085 ... done
Creating springboot-mysql-8084 ... done
Creating springboot-mysql-8083 ... done
mysql      | 2020-12-13T12:40:44.126080Z 1 [System] [MY-013577] [InnoDB] InnoDB initialization has ended.
springboot-mysql-8085 | 
springboot-mysql-8085 |   .   ____          _            __ _ _
springboot-mysql-8085 |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
springboot-mysql-8085 | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
springboot-mysql-8085 |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
springboot-mysql-8085 |   '  |____| .__|_| |_|_| |_\__, | / / / /
springboot-mysql-8085 |  =========|_|==============|___/=/_/_/_/
springboot-mysql-8085 |  :: Spring Boot ::                (v2.4.0)
springboot-mysql-8085 | 
...

```

## 运行结果

- 查看运行的容器和端口号

```
guodong@mars springboot-mysql-docker-compose % docker ps 
CONTAINER ID        IMAGE                                     COMMAND                  CREATED             STATUS              PORTS                               NAMES
76a2e3151718        springboot-mysql-docker-compose_webapp2   "java -cp app:app/li…"   40 seconds ago      Up 25 seconds       8080/tcp, 0.0.0.0:8084->8082/tcp    springboot-mysql-8084
743aa4434653        springboot-mysql-docker-compose_webapp1   "java -cp app:app/li…"   40 seconds ago      Up 25 seconds       8080/tcp, 0.0.0.0:8083->8082/tcp    springboot-mysql-8083
b71dd0f9f28a        springboot-mysql-docker-compose_webapp3   "java -cp app:app/li…"   40 seconds ago      Up 25 seconds       8080/tcp, 0.0.0.0:8085->8082/tcp    springboot-mysql-8085
c8e2f6021a92        mysql:latest                              "docker-entrypoint.s…"   40 seconds ago      Up 39 seconds       0.0.0.0:3306->3306/tcp, 33060/tcp   mysql

```

- webapp应用程序、mysql启动后, 通过不同的端口号访问webapp :

> 因为每个webapp容器启动时都会初始化插入一条记录，所以3个webapp容器全部启动完毕后，example.user默认会有3条记录

```
guodong@mars springboot-mysql-docker-compose % curl localhost:8083/users/all
[{"id":1,"name":"andieguo","email":"andieguo@foxmail.com"},{"id":2,"name":"andieguo","email":"andieguo@foxmail.com"},{"id":3,"name":"andieguo","email":"andieguo@foxmail.com"}]%                                                                
guodong@mars springboot-mysql-docker-compose % curl localhost:8084/users/all
[{"id":1,"name":"andieguo","email":"andieguo@foxmail.com"},{"id":2,"name":"andieguo","email":"andieguo@foxmail.com"},{"id":3,"name":"andieguo","email":"andieguo@foxmail.com"}]%                                                                
guodong@mars springboot-mysql-docker-compose % curl localhost:8085/users/all
[{"id":1,"name":"andieguo","email":"andieguo@foxmail.com"},{"id":2,"name":"andieguo","email":"andieguo@foxmail.com"},{"id":3,"name":"andieguo","email":"andieguo@foxmail.com"}]%                                                                
guodong@mars springboot-mysql-docker-compose % 
                                                                                                                                         
```

- 停止并且移除容器
```
guodong@mars springboot-mysql-docker-compose % docker-compose down
Stopping springboot-mysql-8084 ... done
Stopping springboot-mysql-8083 ... done
Stopping springboot-mysql-8085 ... done
Stopping mysql                 ... done
Removing springboot-mysql-8084 ... done
Removing springboot-mysql-8083 ... done
Removing springboot-mysql-8085 ... done
Removing mysql                 ... done
Removing network springboot-mysql-docker-compose_default
```

## 参考文献

- [docker-compose depends on](https://docs.docker.com/compose/compose-file/#/dependson)
- [Docker容器的重启策略](DOCKER-RESTART.md)