
### 通过Docker部署springboot工程

- 打包

``` bash 
guodong@mars springboot-demo % mvn package spring-boot:repackage
```

- 构建Dockerfile

> VOLUME ["/data"] 创建一个可以从本地主机或其他容器挂载的挂载点；  
> ADD \<src> \<dest> 该命令将复制指定的\<src>到容器中的\<dest>  
> ENTRYPOINT 配置容器启动后执行的命令，每个Dockerfile中只能有一个ENTRYPOINT  
> EXPOSE <port> 告诉Docker服务端容器暴露的端口号  

``` bash
FROM java:8
VOLUME /tmp
ADD springboot-demo-1.0.0.war springboot-demo-1.0.0.war
ENTRYPOINT ["java","-jar","/springboot-demo-1.0.0.war"]
EXPOSE 8082
```

- 创建镜像
> docker build -t springboot-demo src/main/docker/ 命令:   
> 通过-t 指定镜像的标签信息，希望生成镜像标签为springboot-demo
> 指定Dockerfile所在路径为src/main/docker/  

``` bash
guodong@mars springboot-demo % cd springboot-demo
guodong@mars springboot-demo % cp target/springboot-demo-1.0.0.war src/docker/
guodong@mars springboot-demo % docker build -t springboot-demo src/main/docker/
guodong@mars springboot-demo % docker images
REPOSITORY              TAG                 IMAGE ID            CREATED             SIZE
springboot-demo         latest              c5150ce0e9f6        36 minutes ago      660MB
```

- 运行容器

``` bash
guodong@mars springboot-demo % docker run -p 8082:8082 --name springboot-demo -d springboot-demo
guodong@mars springboot-demo % docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
bf56cff314ce        springboot-demo     "java -jar /springbo…"   35 seconds ago      Up 34 seconds       0.0.0.0:8082->8082/tcp   springboot-demo
```

- 访问
http://localhost:8082/home