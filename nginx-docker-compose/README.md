## 使用 docker + compose + nginx 实现负载均衡


### 1.使用docker compose实现nginx部署

- nginx 镜像获取

```
guodong@mars gitworkspace % docker pull nginx
```

- 创建docker-compose.yaml

> 参见：nginx-docker-compose/simple/docker-compose.yaml

- 运行docker-compose

```
guodong@mars nginx-docker-compose % cd simple
guodong@mars simple % docker-compose up  
Creating network "nginx-docker-compose_default" with the default driver
Creating nginx_8085 ... done
Creating nginx_8086 ... done
Creating nginx_8087 ... done
Attaching to nginx_8086, nginx_8085, nginx_8087
```

- 访问nginx

```
guodong@mars simple % curl localhost:8085
this is index,port is 8085
guodong@mars simple % curl localhost:8086
this is index,port is 8086
guodong@mars simple % curl localhost:8087
this is index,port is 8087
```

- 关闭docker-compose 

```
guodong@mars simple % docker-compose down
```

### 2.使用docker compose实现负载均衡

- 创建nginx.conf

> 参见：nginx-docker-compose/balance/nginx.conf

- 创建docker-compose.yaml

> 参见：nginx-docker-compose/balance/docker-compose.yaml

- 启动docker-compose
```
guodong@mars simple % docker-compose down
guodong@mars simple % cd ../balance
guodong@mars balance % docker-compose up
nginx_8087 is up-to-date
nginx_8086 is up-to-date
nginx_8085 is up-to-date
Recreating master ... done
Attaching to nginx_8087, nginx_8086, nginx_8085, master
master    | /docker-entrypoint.sh: /docker-entrypoint.d/ is not empty, will attempt to perform configuration

```

- 运行效果

```
guodong@mars balance % curl http://localhost:8089
this is index,port is 8087
guodong@mars balance % curl http://localhost:8089
this is index,port is 8086
guodong@mars balance % curl http://localhost:8089
this is index,port is 8085
guodong@mars balance % curl http://localhost:8089
this is index,port is 8087
guodong@mars balance % curl http://localhost:8089
this is index,port is 8086
guodong@mars balance % curl http://localhost:8089
this is index,port is 8087
```
- 关闭docker-compose 

```
guodong@mars balance % docker-compose down
```

- 特别注意：

YAML配置：mapping values are not allowed here

在编写配置文件的时候，出现了mapping values not allowed here的错误，原因是的冒号 ”:“后面没有空格。

原因分析：yml文件中，键值对是以":"作为分隔符，而值经常会包含冒号，比如服务器地址。在yaml解析器解析过程中，如果不在键值对中加特殊符号，还真是难以根据键解析出值来。


### 参考文献

- 反向代理
- nginx文件解析