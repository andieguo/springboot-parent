### 通过Docker部署springboot工程

- 打包

``` bash 
guodong@mars springboot-slf4j-docker % mvn package
```

- 构建[Dockerfile](src/docker/Dockerfile)

> VOLUME ["/data"] 创建一个可以从本地主机或其他容器挂载的挂载点；  
> ADD \<src> \<dest> 该命令将复制指定的\<src>到容器中的\<dest>  
> ENTRYPOINT 配置容器启动后执行的命令，每个Dockerfile中只能有一个ENTRYPOINT  
> EXPOSE <port> 告诉Docker服务端容器暴露的端口号  

``` bash
FROM java:8
VOLUME /tmp
VOLUME /logs/
ADD springboot-slf4j-docker-1.0.0.war springboot-slf4j-docker-1.0.0.war
ENTRYPOINT ["java","-jar","/springboot-slf4j-docker-1.0.0.war"]
EXPOSE 8082
```

- 创建镜像
> docker build -t springboot-slf4j-docker src/main/docker/ 命令:   
> 通过-t 指定镜像的标签信息，希望生成镜像标签为springboot-slf4j-docker
> 指定Dockerfile所在路径为src/docker/  

``` bash
guodong@mars springboot-slf4j-docker % cd springboot-slf4j-docker
guodong@mars springboot-slf4j-docker % cp target/springboot-slf4j-docker-1.0.0.war src/docker/
guodong@mars springboot-slf4j-docker % docker build -t springboot-slf4j-docker src/docker/
guodong@mars springboot-slf4j-docker % docker images
REPOSITORY                       TAG                 IMAGE ID            CREATED             SIZE
springboot-slf4j-docker         latest              c5150ce0e9f6        36 minutes ago      660MB
```

- 运行容器

> -p 8082:8082 端口映射
> -v /Users/guodong/logs/:/logs/ 日志挂载

``` bash
guodong@mars springboot-slf4j-docker % docker run -p 8082:8082 -v /Users/guodong/logs/:/logs/ --name springboot-slf4j-docker -d springboot-slf4j-docker
guodong@mars springboot-slf4j-docker % docker ps
CONTAINER ID        IMAGE                        COMMAND                  CREATED             STATUS              PORTS                    NAMES
bf56cff314ce        springboot-slf4j-docker     "java -jar /springbo…"   35 seconds ago      Up 34 seconds       0.0.0.0:8082->8082/tcp   springboot-slf4j-docker
```

- 访问


``` bash
guodong@mars springboot-slf4j-docker % curl http://localhost:8082/home
HELLO SPRINT BOOT!%                                                                                  
```

- 在本地查看日志

```
guodong@mars springboot-slf4j-docker % cd /Users/guodong/logs
guodong@mars logs % pwd
/Users/guodong/logs
guodong@mars logs % ls -la
total 24
drwxr-xr-x   5 guodong  staff   160 12 27 16:44 .
drwxr-xr-x+ 28 guodong  staff   896 12 27 16:45 ..
-rw-r--r--   1 guodong  staff   102 12 27 16:45 app.log
-rw-r--r--   1 guodong  staff  2538 12 27 16:45 springboot-slf4j-docker.log
-rw-r--r--   1 guodong  staff    79 12 27 16:45 web.log
guodong@mars logs % cat app.log 
5384 [http-nio-8082-exec-1] INFO  c.a.s.controller.HomeController - access home page,print applogger!
guodong@mars logs % cat web.log 
5388 [http-nio-8082-exec-1] INFO  webLogger - access home page,print webLogger

```

- 关闭并删除容器

```
guodong@mars springboot-slf4j-docker % docker stop springboot-slf4j-docker
guodong@mars springboot-slf4j-docker % docker rm springboot-slf4j-docker

```
