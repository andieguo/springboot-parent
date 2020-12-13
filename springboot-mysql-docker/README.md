# springboot-mysql-docker

本工程是关于SpringBoot与mysql集成，通过docker环境模拟分布式部署的入门级demo工程

### Docker部署mysql单机环境、SpringBoot应用

#### Docker部署mysql单机环境

镜像地址：https://hub.docker.com/_/mysql?tab=tags 

• 获取最新版本镜像

``` bash
guodong@mars ~ % docker pull mysql
Using default tag: latest
latest: Pulling from library/mysql
852e50cd189d: Pull complete 
29969ddb0ffb: Pull complete 
a43f41a44c48: Pull complete 
5cdd802543a3: Pull complete 
b79b040de953: Pull complete 
938c64119969: Pull complete 
7689ec51a0d9: Pull complete 
a880ba7c411f: Pull complete 
984f656ec6ca: Pull complete 
9f497bce458a: Pull complete 
b9940f97694b: Pull complete 
2f069358dc96: Pull complete 
Digest: sha256:4bb2e81a40e9d0d59bd8e3dc2ba5e1f2197696f6de39a91e90798dd27299b093
Status: Downloaded newer image for mysql:latest
docker.io/library/mysql:latest

guodong@mars ~ % docker image ls
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
mysql               latest              dd7265748b5d        8 days ago          545MB
alpine/git          latest              76a4083eacef        8 days ago          28.4MB
```

• 启动mysql容器

> -i：以交互模式运行，通常配合-t
> -t：为容器重新分配一个伪输入终端，通常配合-i
> -d：后台运行容器
> -p：端口映射，格式为主机端口:容器端口
> -e：设置环境变量，这里设置的是root密码
> --name：设置容器别名

``` bash
guodong@mars ~ % docker run -p 3306:3306 --name mysql-v2 -e MYSQL_ROOT_PASSWORD=root -d mysql    
2b444ea8ceac0076d276f565b8c98a91bb741652957aef277c7d0a83699fb287
Last login: Sun Nov 29 13:34:17 on ttys000
```

• 连接到mysql容器的伪终端

> 链接到mysql容器伪终端后，通过mysql -r root -p访问mysql服务端，默认密码root
> 创建数据库example;

``` bash
guodong@mars ~ % docker exec -it 2b444ea8ceac0076d276f565b8c98a91bb741652957aef277c7d0a83699fb287 /bin/sh; exit
# ls
bin   docker-entrypoint-initdb.d  home	 media	proc  sbin  tmp
boot  entrypoint.sh		  lib	 mnt	root  srv   usr
dev   etc			  lib64  opt	run   sys   var
# mysql -r root -p
Enter password: 
ERROR 1045 (28000): Access denied for user 'root'@'localhost' (using password: NO)
# mysql -u root -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 11
Server version: 8.0.22 MySQL Community Server - GPL

Copyright (c) 2000, 2020, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> create database example; 
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| example            |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.00 sec)

```

#### 本地部署SpringBoot应用

> 本地部署SpringBoot应用 和 Docker环境部署SpringBoot应用 是相互独立的，本地环境只能部署一台webapp，Docker环境部署可以将mysql和多台webapp同时部署在docker环境中，用于模拟分布式环境；

- mvn命令打包springboot应用

``` bash 
guodong@mars springboot-mysql-docker % mvn package
```

- 本地启动springboot应用

> 2020-12-13 12:08:35.408  INFO 15242 --- [           main] c.a.springboot.SpringBootWebApplication  : No active profile set, falling back to default profiles: default  
> 本地启动没有指定profile，默认加载application.properties文件，使用localhost作为mysql服务器host

``` bash 
guodong@mars springboot-mysql-docker % java -jar target/springboot-mysql-docker-1.0.0.jar
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.0)

2020-12-13 12:38:38.379  INFO 15532 --- [           main] c.a.springboot.SpringBootWebApplication  : Starting SpringBootWebApplication v1.0.0 using Java 1.8.0_201 on mars.local with PID 15532 (/Users/guodong/Documents/gitworkspace/springboot-parent/springboot-mysql-docker/target/springboot-mysql-docker-1.0.0.jar started by guodong in /Users/guodong/Documents/gitworkspace/springboot-parent/springboot-mysql-docker)
2020-12-13 12:38:38.382  INFO 15532 --- [           main] c.a.springboot.SpringBootWebApplication  : No active profile set, falling back to default profiles: default

2020-12-13 12:38:42.573  INFO 15532 --- [           main] DeferredRepositoryInitializationListener : Spring Data repositories initialized!
2020-12-13 12:38:42.582  INFO 15532 --- [           main] c.a.springboot.SpringBootWebApplication  : Started SpringBootWebApplication in 4.623 seconds (JVM running for 5.073)

```

- 访问web应用
```
guodong@mars springboot-mysql-docker % curl localhost:8082/home
Stay hungry, stay foolish                                                                                                                   
guodong@mars springboot-mysql-docker % curl localhost:8082/users/all 
[{"id":1,"name":"andyguo.gd","email":"andieguo@foxmail.com"},{"id":2,"name":"jack","email":"jack@foxmail.com"},{"id":3,"name":"jack","email":"jack"},{"id":4,"name":"jack1","email":"jack"}]                                                                           
```

- 关闭web应用
> 关闭本地web应用，释放8082端口


#### Docker部署SpringBoot应用

> 通过Docker环境部署多个spring boot webapp应用，模拟分布式环境

- mvn命令打包springboot应用

``` bash 
guodong@mars springboot-mysql-docker % mvn package
```

- 构建[Dockerfile](src/docker/Dockerfile)

> 1) VOLUME ["/data"] 创建一个可以从本地主机或其他容器挂载的挂载点；  
> 2) ADD \<src> \<dest> 该命令将复制指定的\<src>到容器中的\<dest>  
> 3) ENTRYPOINT 配置容器启动后执行的命令，每个Dockerfile中只能有一个ENTRYPOINT
> - java -jar -Dspring.profiles.active=docker /springboot-mysql-docker-1.0.0.jar 
> - java命令指定SpringBoot的profile环境为docker，启动时加载application-docker.properties
> 4) EXPOSE <port> 告诉Docker服务端容器暴露的端口号  

``` bash
FROM java:8
VOLUME /tmp
ADD springboot-mysql-docker-1.0.0.jar springboot-mysql-docker-1.0.0.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","/springboot-mysql-docker-1.0.0.jar"]
EXPOSE 8082
```

- 创建镜像
> docker build -t springboot-mysql-docker src/docker/ 命令:     
> 1) 通过-t 指定镜像的标签信息，希望生成镜像标签为springboot-mysql-docker  
> 2) 指定Dockerfile所在路径为src/docker/    

``` bash
guodong@mars springboot-mysql-docker % cd springboot-mysql-docker
guodong@mars springboot-mysql-docker % cp target/springboot-mysql-docker-1.0.0.jar src/docker/
guodong@mars springboot-mysql-docker % docker build -t springboot-mysql-docker src/docker/ 
Sending build context to Docker daemon  38.64MB
Step 1/5 : FROM java:8
 ---> d23bdf5b1b1b
Step 2/5 : VOLUME /tmp
 ---> Using cache
 ---> 269067bcaa38
Step 3/5 : ADD springboot-mysql-docker-1.0.0.jar springboot-mysql-docker-1.0.0.jar
 ---> 94d018d1f594
Step 4/5 : ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","/springboot-mysql-docker-1.0.0.jar"]
 ---> Running in 1bfb2378f567
Removing intermediate container 1bfb2378f567
 ---> 3f1229e85cc9
Step 5/5 : EXPOSE 8082
 ---> Running in 6ee8f5590be5
Removing intermediate container 6ee8f5590be5
 ---> b0e277cfb0bd
Successfully built b0e277cfb0bd
Successfully tagged springboot-mysql-docker:latest
guodong@mars springboot-mysql-docker % docker images
REPOSITORY                                      TAG                 IMAGE ID            CREATED             SIZE
springboot-mysql-docker                         latest              b0e277cfb0bd        26 seconds ago      682MB
```

- 运行容器  
分别启动3个webapp容器,端口号分别为8082、8083、8084
> link 是在两个contain之间建立一种父子关系，父container中的web，可以得到子container db上的信息。
  通过link的方式创建容器，我们可以使用被Link容器的别名进行访问，而不是通过IP，解除了对IP的依赖。
  不过，link的方式只能解决单机容器间的互联，多机的情况下，需要通过别的方式进行连接。
  --link=container_name or id:name 使用这个选项在你运行一个容器时，可以在此容器的/etc/hosts文件中增加一个额外的name主机名，这个名字为container_name的容器的IP地址的别名。这使得新容器的内部进程可以访问主机名为name的容器而不用知道它的Ip。
  内网是走docker0的网桥，互相之间是Ping的通的，但是docker run 建立容器的时候，它的Ip地址是不可控制的，所以docker 用link的方式使web能够访问到db中的数据。

``` bash
guodong@mars springboot-mysql-docker % docker run -p 8082:8082 --name springboot-mysql-docker-8082 --link mysql-v2:db -d springboot-mysql-docker
guodong@mars springboot-mysql-docker % docker run -p 8083:8082 --name springboot-mysql-docker-8083 --link mysql-v2:db -d springboot-mysql-docker
guodong@mars springboot-mysql-docker % docker run -p 8084:8082 --name springboot-mysql-docker-8084 --link mysql-v2:db -d springboot-mysql-docker
guodong@mars springboot-mysql-docker % docker ps
```

- 访问web应用 

```
guodong@mars springboot-mysql-docker % curl localhost:8082/users/all
[{"id":1,"name":"andyguo.gd","email":"andieguo@foxmail.com"},{"id":2,"name":"jack","email":"jack@foxmail.com"},{"id":3,"name":"jack","email":"jack"},{"id":4,"name":"jack1","email":"jack"}]%                                                                                             
guodong@mars springboot-mysql-docker % curl localhost:8083/users/all
[{"id":1,"name":"andyguo.gd","email":"andieguo@foxmail.com"},{"id":2,"name":"jack","email":"jack@foxmail.com"},{"id":3,"name":"jack","email":"jack"},{"id":4,"name":"jack1","email":"jack"}]%                                                                                             
guodong@mars springboot-mysql-docker % curl localhost:8084/users/all
[{"id":1,"name":"andyguo.gd","email":"andieguo@foxmail.com"},{"id":2,"name":"jack","email":"jack@foxmail.com"},{"id":3,"name":"jack","email":"jack"},{"id":4,"name":"jack1","email":"jack"}]%                                                                                                                                                                                            
```
 

### SpringBoot与mysql集成解读
1.SpringBoot与web集成

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

```

2.SpringBoot与Mysql集成

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```
#### 基本配置

#### 自定义参数配置

#### starter源码分析


## 参考文献
- [accessing-data-mysql](https://spring.io/guides/gs/accessing-data-mysql/)
- [Spring Data Jpa的使用](https://www.jianshu.com/p/c23c82a8fcfc)