## nginx-docker-compose

本工程使用 docker + compose + nginx 实现负载均衡

### 使用docker compose实现nginx部署

- nginx 镜像获取

```
guodong@mars gitworkspace % docker pull nginx
```

- 创建[_docker-compose.yaml_](simple/docker-compose.yaml)

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

### 使用docker compose实现负载均衡

- 创建[_nginx.conf_](balance/nginx-master/conf/nginx.conf)

> upstream: 使用加权负载均衡，权重越大的服务器，被分配到的次数就会越多，通常用于后端服务器性能不一致的情况。  
> proxy_pass：指定需要反向代理的服务器地址，可以是一个upstream池

```
    upstream www.andieguo.com {
        server nginx_8085 weight=1;
        server nginx_8086 weight=2;
        server nginx_8087 weight=3;
    }

    server {
        listen       80;
        server_name  localhost;

        location / {
            root   html;
            index  index.html index.htm;
            # 反向代理
            proxy_pass   http://www.andieguo.com;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
```

- 创建[_docker-compose.yaml_](balance/docker-compose.yaml)

> docker hub的nginx镜像可挂载的volume如下：
> - 日志位置：/var/log/nginx/  
> - 配置文件位置：/etc/nginx/  
> - 项目位置：/usr/share/nginx/html  

```
version: "3"

services:
 web_1:
  container_name: nginx_8085
  image: nginx
  volumes:
   - ./html-8085/:/usr/share/nginx/html
   - ./log/:/var/log/nginx/
  ports:
   - "8085:80"

 ...

 master:
  container_name: master
  image: nginx
  volumes:
   - ./nginx.conf:/etc/nginx/nginx.conf
   - ./log/:/var/log/nginx/
  ports:
   - "8089:80"
  depends_on:
   - web_1
   - web_2
   - web_3

```

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

- [upstream与proxy_pass实现反向代理配置教程](http://www.linuxe.cn/post-182.html)
- [Nginx访问日志（access_log）配置及信息详解](https://www.cnblogs.com/czlun/articles/7010591.html)
- [当初我要是这么学习Nginx就好了！（多图详解）](https://www.jianshu.com/p/e90050dc89b6)
- [Nginx 动态负载 upstream 三种方案](https://blog.51cto.com/qiangsh/2021399)
