# springboot-redis-docker
本工程是关于SpringBoot与Redis集成的入门级demo工程

### 什么是Redis

　　Redis 是一个速度非常快的非关系数据库（Non-Relational Database），它可以存储键（Key）与 多种不同类型的值（Value）之间的映射（Mapping），可以将存储在内存的键值对数据持久化到硬盘，可以使用复制特性来扩展读性能，还可以使用客户端分片来扩展写性能。Redis主要有以下几个优点：

　　1 性能极高，它每秒可执行约 100,000 个 Set 以及约 100,000 个 Get 操作；

　　2 丰富的数据类型，Redis 对大多数开发人员已知的大多数数据类型提供了原生支持，这使得各种问题得以轻松解决；

　　3 原子性，因为所有 Redis 操作都是原子性的，所以多个客户端会并发地访问一个 Redis 服务器，获取相同的更新值；

　　4 丰富的特性，Redis 是一个多效用工具，有非常多的应用场景，包括缓存、消息队列（Redis 原生支持发布/订阅）、短期应用程序数据（比如 Web 会话、Web 页面命中计数）等。

　　目前我们常用的Value的数据类型有String(字符串)，Hash(哈希)，List(列表)，Set(集合)，Zset(有序集合)。


### Docker部署Redis单机环境、SpringBoot应用

#### Docker部署Redis单机环境

镜像地址：https://hub.docker.com/_/redis?tab=tags 

• 获取最新版本镜像

``` bash
guodong@mars ~ % docker pull redis
Using default tag: latest
latest: Pulling from library/redis
852e50cd189d: Already exists 
76190fa64fb8: Pull complete 
9cbb1b61e01b: Pull complete 
d048021f2aae: Pull complete 
6f4b2af24926: Pull complete 
1cf1d6922fba: Pull complete 
Digest: sha256:5b98e32b58cdbf9f6b6f77072c4915d5ebec43912114031f37fa5fa25b032489
Status: Downloaded newer image for redis:latest
docker.io/library/redis:latest
guodong@mars ~ % docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
redis               latest              74d107221092        2 weeks ago         104MB

```

• 启动redis容器

> -p 6379:6379：映射容器服务的 6379 端口到宿主机的 6379 端口。外部可以直接通过宿主机ip:6379 访问到 Redis 的服务。

``` bash
guodong@mars ~ % docker run -itd --name redis -p 6379:6379 redis
33fc27668e5cc28fb002032227cc9f772b8931348330ef55c9a8c5bc292899eb
```

• 连接到容器的伪终端

``` bash
Last login: Sun Dec  6 10:45:44 on ttys000
guodong@mars ~ % docker exec -it 33fc27668e5cc28fb002032227cc9f772b8931348330ef55c9a8c5bc292899eb /bin/sh; exit
# redis-cli
127.0.0.1:6379> set test 1
OK
127.0.0.1:6379> get test
"1"
127.0.0.1:6379> set test 2
OK
127.0.0.1:6379> get test
"2"

```

#### 本地部署SpringBoot应用

> 本地部署SpringBoot应用 和 Docker环境部署SpringBoot应用 是相互独立的，本地环境只能部署一台webapp，Docker环境可以部署多台webapp用于模拟分布式环境；

- mvn命令打包springboot应用

``` bash 
guodong@mars springboot-redis-docker % mvn package
```

- 本地启动springboot应用

> 2020-12-13 12:08:35.408  INFO 15242 --- [           main] c.a.springboot.SpringBootWebApplication  : No active profile set, falling back to default profiles: default  
> 本地启动没有指定profile，默认加载application.properties文件，使用localhost作为spring.redis.host

``` bash 
guodong@mars springboot-redis-docker % java -jar target/springboot-redis-docker-1.0.0.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.0)

2020-12-13 12:08:35.405  INFO 15242 --- [           main] c.a.springboot.SpringBootWebApplication  : Starting SpringBootWebApplication v1.0.0 using Java 1.8.0_201 on mars.local with PID 15242 (/Users/guodong/Documents/gitworkspace/springboot-parent/springboot-redis-docker/target/springboot-redis-docker-1.0.0.jar started by guodong in /Users/guodong/Documents/gitworkspace/springboot-parent/springboot-redis-docker)
2020-12-13 12:08:35.408  INFO 15242 --- [           main] c.a.springboot.SpringBootWebApplication  : No active profile set, falling back to default profiles: default
2020-12-13 12:08:36.187  INFO 15242 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode!
2020-12-13 12:08:36.190  INFO 15242 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2020-12-13 12:08:38.201  INFO 15242 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8082 (http) with context path ''
2020-12-13 12:08:38.212  INFO 15242 --- [           main] c.a.springboot.SpringBootWebApplication  : Started SpringBootWebApplication in 3.413 seconds (JVM running for 3.833)

```

- 访问web应用
```
guodong@mars springboot-redis-docker % curl localhost:8082
the current page ipAddress is 127.0.0.1
the current page has been accessed 13 times                                                                                                
guodong@mars springboot-redis-docker % curl localhost:8082
the current page ipAddress is 127.0.0.1
the current page has been accessed 14 times                                                                                                
```

- 关闭web应用
> 关闭本地web应用，释放8082端口


#### Docker部署SpringBoot应用

> 通过Docker环境部署多个spring boot webapp应用，模拟分布式环境

- mvn命令打包springboot应用

``` bash 
guodong@mars springboot-redis-docker % mvn package
```

- 构建[Dockerfile](src/docker/Dockerfile)

> 1) VOLUME ["/data"] 创建一个可以从本地主机或其他容器挂载的挂载点；  
> 2) ADD \<src> \<dest> 该命令将复制指定的\<src>到容器中的\<dest>  
> 3) ENTRYPOINT 配置容器启动后执行的命令，每个Dockerfile中只能有一个ENTRYPOINT
> - java -jar -Dspring.profiles.active=redis /springboot-redis-docker-1.0.0.jar 
> - java命令指定SpringBoot的profile环境为redis，启动时加载application-redis.properties
> 4) EXPOSE <port> 告诉Docker服务端容器暴露的端口号  

``` bash
FROM java:8
VOLUME /tmp
ADD springboot-redis-docker-1.0.0.jar springboot-redis-docker-1.0.0.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=redis","/springboot-redis-docker-1.0.0.jar"]
EXPOSE 8082
```

- 创建镜像
> docker build -t springboot-redis-docker src/docker/ 命令:     
> 1) 通过-t 指定镜像的标签信息，希望生成镜像标签为springboot-redis-docker  
> 2) 指定Dockerfile所在路径为src/docker/    

``` bash
guodong@mars springboot-redis-docker % cd springboot-redis-docker
guodong@mars springboot-redis-docker % cp target/springboot-redis-docker-1.0.0.jar src/docker/
guodong@mars springboot-redis-docker % docker build -t springboot-redis-docker src/docker/
guodong@mars springboot-redis-docker % docker images
REPOSITORY                       TAG                 IMAGE ID            CREATED             SIZE
springboot-redis-docker         latest              c5150ce0e9f6        36 minutes ago      660MB
```

- 运行容器  
分别启动3个webapp容器,端口号分别为8082、8083、8084
> link 是在两个contain之间建立一种父子关系，父container中的web，可以得到子container db上的信息。
  通过link的方式创建容器，我们可以使用被Link容器的别名进行访问，而不是通过IP，解除了对IP的依赖。
  不过，link的方式只能解决单机容器间的互联，多机的情况下，需要通过别的方式进行连接。
  --link=container_name or id:name 使用这个选项在你运行一个容器时，可以在此容器的/etc/hosts文件中增加一个额外的name主机名，这个名字为container_name的容器的IP地址的别名。这使得新容器的内部进程可以访问主机名为name的容器而不用知道它的Ip。
  内网是走docker0的网桥，互相之间是Ping的通的，但是docker run 建立容器的时候，它的Ip地址是不可控制的，所以docker 用link的方式使web能够访问到db中的数据。

``` bash
guodong@mars springboot-redis-docker % docker run -p 8082:8082 --name springboot-redis-docker-8082 --link redis:redis -d springboot-redis-docker
guodong@mars springboot-redis-docker % docker run -p 8083:8082 --name springboot-redis-docker-8083 --link redis:redis -d springboot-redis-docker
guodong@mars springboot-redis-docker % docker run -p 8084:8082 --name springboot-redis-docker-8084 --link redis:redis -d springboot-redis-docker
guodong@mars springboot-redis-docker % docker ps
```

- 访问web应用 

```
guodong@mars springboot-redis-docker % curl localhost:8082
the current page ipAddress is 172.17.0.4
the current page has been accessed 17 times%                                                                                                 
guodong@mars springboot-redis-docker % curl localhost:8083
the current page ipAddress is 172.17.0.5
the current page has been accessed 18 times%                                                                                                 
guodong@mars springboot-redis-docker % curl localhost:8084
the current page ipAddress is 172.17.0.6
the current page has been accessed 19 times%                                                                                                 
```
 

### SpringBoot与Redis集成解读
1.SpringBoot与web集成

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

```

2.SpringBoot与Redis集成

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>2.4.0</version>
</dependency>
<!--Redis自定义配置需要该配置-->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.9.0</version>
</dependency>
```
#### 基本配置

#### 自定义参数配置

#### starter源码分析

- spring.factory RedisRepositoryFactory

- RedisConnectionFactory实现类：LettuceConnectionFactory


#### 待办事项

- redis常用命令
- docker + redis分布式集群搭建