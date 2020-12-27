

# 配置 logback

在应用程序当中使用日志语句需要耗费大量的精力。根据调查，大约有百分之四的代码用于打印日志。即使在一个中型应用的代码当中也有成千上万条日志的打印语句。考虑到这种情况，我们需要使用工具来管理这些日志语句。

可以通过编程或者配置 XML 脚本或者 Groovy 格式的方式来配置 logback。对于已经使用 log4j 的用户可以通过这个工具来把 log4j.properties 转换为 logback.xml。

以下是 logback 的初始化步骤：

- logback 会在类路径下寻找名为 logback-test.xml 的文件。
- 如果没有找到，logback 会继续寻找名为 logback.groovy 的文件。
- 如果没有找到，logback 会继续寻找名为 logback.xml 的文件。
- 如果没有找到，将会通过 JDK 提供的 ServiceLoader 工具在类路径下寻找文件 META-INFO/services/ch.qos.logback.classic.spi.Configurator，该文件的内容为实现了 Configurator 接口的实现类的全限定类名。
- 如果以上都没有成功，logback 会通过 BasicConfigurator 为自己进行配置，并且日志将会全部在控制台打印出来。

最后一步的目的是为了保证在所有的配置文件都没有被找到的情况下，提供一个默认的（但是是非常基础的）配置。

## 配置文件的语法

logback 允许你重新定义日志的行为而不需要重新编译代码，你可以轻易的禁用调应用中某些部分的日志，或者将日志输出到任何地方。

logback 的配置文件非常的灵活，不需要指定 DTD 或者 xml 文件需要的语法。但是，最基本的结构为 <configuration> 元素，包含 0 或多个 <appender> 元素，其后跟 0 或多个 <logger> 元素，其后再跟最多只能存在一个的 <root> 元素。

```
|-configuration
|-- appender
|-- logger
|-- root
```


### 配置 logger

通过 \<logger\> 标签来过 logger 进行配置，一个 \<logger\> 标签必须包含一个 name 属性，一个可选的 level 属性，一个可选 additivity 属性。additivity 的值为 true 或 false。level 的值为 TRACE，DEBUG，INFO，WARN，ERROR，ALL，OFF，INHERITED，NULL。当 level 的值为 INHERITED 或 NULL 时，将会强制 logger 继承上一层的级别。

\<logger\> 元素至少包含 0 或多个 \<appender-ref\> 元素。每一个 appender 通过这种方式被添加到 logger 上。与 log4j 不同的是，logbakc-classic 不会关闭或移除任何之前在 logger 上定义好的的 appender。

### 配置 root logger

root logger 通过 \<root\> 元素来进行配置。它只支持一个属性——level。它不允许设置其它任何的属性，因为 additivity 并不适用 root logger。而且，root logger 的名字已经被命名为 "ROOT"，也就是说也不支持 name 属性。level 属性的值可以为：TRACE、DEBUG、INFO、WARN、ERROR、ALL、OFF，但是不能设置为 INHERITED 或 NULL。

跟 \<logger\> 元素类似，\<root\> 元素可以包含 0 或多个 \<appender-ref\> 元素。


### 变量的定义

logback 支持变量的定义以及替换，变量有它的作用域。而且，变量可以在配置文件中，外部文件中，外部资源文件中，甚至动态定义。

### 配置Appender

appender 通过 \<appender\> 元素进行配置，需要两个强制的属性 name 与 class。name 属性用来指定 appender 的名字，class 属性需要指定类的全限定名用于实例化。<appender> 元素可以包含 0 或一个 \<layout\> 元素，0 或多个 \<encoder\> 元素，0 或多个 \<filter\> 元素。除了这些公共的元素之外，\<appender\> 元素可以包含任意与 appender 类的 JavaBean 属性相一致的元素。

\<layout\> 元素强制一个 class 属性去指定一个类的全限定名，用于实例化。与 \<appender\> 元素一样，\<layout\> 元素也可以包含与 layout 实例相关的属性。如果 layout 的 class 是 PatternLayout，那么 class 属性可以被隐藏掉（参考：默认类映射），因为这个很常见。.

\<encoder\> 元素强制一个 class 属性去指定一个类的全限定名，用于实例化。如果 encoder 的 class 是 PatternLayoutEncoder，那么基于默认类映射，class 属性可以被隐藏。

#### RollingFileAppender
     
RollingFileAppender 继承自FileAppender，具有轮转日志文件的功能。例如，RollingFileAppender 将日志输出到 log.txt 文件，在满足了特定的条件之后，将日志输出到另外一个文件。

与 RollingFileAppender 进行交互的有两个重要的子组件。第一个是 RollingPolicy，它负责日志轮转的功能。另一个是 TriggeringPolicy，它负责日志轮转的时机。所以 RollingPolicy 负责发生什么，TriggeringPolicy 负责什么时候发生。

为了让 RollingFileAppender 生效，必须同时设置 RollingPolicy 与 TriggeringPolicy。但是，如果 RollingPolicy 也实现了 TriggeringPolicy 接口，那么只需要设置前一个就好了。


#### 基于时间的轮转策略
TimeBasedRollingPolicy 是最常用的轮转策略。它是基于时间来定义轮转策略。例如按天或者按月。TimeBasedRollingPolicy 既负责轮转的行为，也负责触发轮转。实际上，TimeBasedRollingPolicy 同时实现了 RollingPolicy 与 TriggeringPolicy 接口。

TimeBasedRollingPolicy支持通过maxHistory和totalSizeCap配置轮转策略，maxHistory配置日志保存的时间范围，totalSizeCap配置日志总文件大小；
```
 <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logFile.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
<!--             按天轮转 -->
            <fileNamePattern>logFile.%d{yyyy-MM-dd}.log</fileNamePattern>
<!--             保存 30 天的历史记录，最大大小为 30GB -->
            <maxHistory>30</maxHistory>
            <totalSizeCap>3GB</totalSizeCap>
        </rollingPolicy>

        <encoder>
            <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender>
```

#### 基于大小以及时间的轮转策略
     
有时你希望按时轮转，但同时又想限制每个日志文件的大小。特别是如果后期处理工具需要对日志进行大小限制。为了满足这个需求，logback 配备了 SizeAndTimeBasedRollingPolicy。
- TimeBasedRollingPolicy 可以通过totalSizeCap限制归档文件总的大小。
- SizeAndTimeBasedRollingPolicy 继承TimeBasedRollingPolicy，支持通过maxFileSize设置每个文件的大小；
     
```
<appender name="APP" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!--按天轮转 -->
            <fileNamePattern>app.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!--保存 7 天的历史记录，最大大小为 1GB -->
            <maxHistory>7</maxHistory>
            <!--该滚动策略日志的总大小，超过的日志会被清除-->
            <totalSizeCap>1GB</totalSizeCap>
            <!--每个文件的大小限制-->
            <maxFileSize>100MB</maxFileSize>
        </rollingPolicy>

        <encoder>
            <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender>
```

# 应用docker部署

[通过Docker部署springboot-slf4j-docker工程](Docker.md)

# 参考文献

- http://www.logback.cn