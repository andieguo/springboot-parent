
### 通过Docker部署springboot工程

- 打包

``` bash 
guodong@mars springboot-webapp-docker % mvn package spring-boot:repackage
```

- 构建[Dockerfile](src/docker/Dockerfile)

> VOLUME ["/data"] 创建一个可以从本地主机或其他容器挂载的挂载点；  
> ADD \<src> \<dest> 该命令将复制指定的\<src>到容器中的\<dest>  
> ENTRYPOINT 配置容器启动后执行的命令，每个Dockerfile中只能有一个ENTRYPOINT  
> EXPOSE <port> 告诉Docker服务端容器暴露的端口号  

``` bash
FROM java:8
VOLUME /tmp
ADD springboot-webapp-docker-1.0.0.war springboot-webapp-docker-1.0.0.war
ENTRYPOINT ["java","-jar","/springboot-webapp-docker-1.0.0.war"]
EXPOSE 8082
```

- 创建镜像
> docker build -t springboot-webapp-docker src/main/docker/ 命令:   
> 通过-t 指定镜像的标签信息，希望生成镜像标签为springboot-webapp-docker
> 指定Dockerfile所在路径为src/main/docker/  

``` bash
guodong@mars springboot-webapp-docker % cd springboot-webapp-docker
guodong@mars springboot-webapp-docker % cp target/springboot-webapp-docker-1.0.0.war src/docker/
guodong@mars springboot-webapp-docker % docker build -t springboot-webapp-docker src/main/docker/
guodong@mars springboot-webapp-docker % docker images
REPOSITORY                       TAG                 IMAGE ID            CREATED             SIZE
springboot-webapp-docker         latest              c5150ce0e9f6        36 minutes ago      660MB
```

- 运行容器

``` bash
guodong@mars springboot-webapp-docker % docker run -p 8082:8082 --name springboot-webapp-docker -d springboot-webapp-docker
guodong@mars springboot-webapp-docker % docker ps
CONTAINER ID        IMAGE                        COMMAND                  CREATED             STATUS              PORTS                    NAMES
bf56cff314ce        springboot-webapp-docker     "java -jar /springbo…"   35 seconds ago      Up 34 seconds       0.0.0.0:8082->8082/tcp   springboot-webapp-docker
```

- 访问
http://localhost:8082/home