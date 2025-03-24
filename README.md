# Spring AI MCP Server 示例项目

这个demo使用Spring AI框架实现一个简单的Model Context Protocol (MCP) Server，包含两个功能模块：计算器服务和天气查询服务。

## demo介绍

Model Context Protocol (MCP) 是Spring AI提供的一种协议，允许大语言模型(LLM)通过标准化的接口调用外部工具和服务。这个Demo实现了两个MCP服务：

1. **计算器服务**：接收数学表达式并返回计算结果
2. **天气查询服务**：接收城市名称并返回该城市的实时天气信息

## 技术栈

- Java 17
- Spring Boot 3.4.4
- Spring AI 1.0.0-M6
- Maven
- Hutool 5.8.36

## 项目结构

```
src/main/java/donts/ai/
├── DontsAiApplication.java         # 应用程序入口
├── demo/                           # 演示功能包
│   ├── mcp_resources/              # MCP资源配置
│   │   ├── ResourceConfig.java     # 资源配置类
│   │   └── SystemResource.java     # 系统资源类
│   └── tools/                      # 工具服务
│       ├── ToolConfig.java         # 工具配置类
│       ├── calculator/             # 计算器服务
│       │   ├── CalculatorFunctionRequest.java
│       │   └── CalculatorMcpServer.java
│       └── weather/                # 天气查询服务
│           ├── WeatherApiProperties.java
│           ├── WeatherFunctionRequest.java
│           ├── WeatherFunctionResponse.java
│           └── WeatherMcpServer.java
```

## 功能说明

### 计算器服务

计算器服务使用Spring Expression Language (SpEL)计算数学表达式。为了避免Java整数除法的问题（如3/5=0），添加了预处理逻辑，将所有整数转换为浮点数。

示例：
- 输入：`3 + 4 * 2`
- 输出：`表达式：3 + 4 * 2\n计算结果：11.0`

### 天气查询服务

天气查询服务使用和风天气API获取城市的实时天气信息。首先通过城市名称查询城市ID，然后使用城市ID获取实时天气数据。

示例：
- 输入：`昆明`
- 输出：城市昆明的实时天气信息（JSON格式）

### MCP 资源

MCP 资源（Resource）是 Model Context Protocol 协议中的一个重要概念，代表服务器想要提供给客户端的任何类型的数据。本项目实现了以下资源：

#### 系统信息资源

系统信息资源提供关于运行环境的基本信息，包括：

- Java 运行时版本
- 操作系统信息（名称、版本、架构）
- 处理器数量
- 当前时间戳

资源URI：`system://info`
MIME类型：`application/json`

示例输出：
```json
{
  "javaVersion": "17.0.10",
  "osName": "Mac OS X",
  "osVersion": "14.4",
  "osArch": "aarch64",
  "processors": 10,
  "timestamp": 1711301234567
}
```

## 配置说明
注意，我之后要使用windsurf来调用这个MCP Server，由于windsurf只支持STDIO的输出模式，所以需要把web
application type设置为none，并且需要设置logging.pattern.console=空的，防止日志打印到控制台，否则windsurf就接受不到tool输出的结果

[windsurf MCP官方文档](https://docs.codeium.com/windsurf/mcp)


### application.properties

```properties
# Required STDIO Configuration
spring.main.web-application-type=none
spring.main.banner-mode=off
logging.pattern.console=

# Server Configuration
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.name=my-weather-server
spring.ai.mcp.server.version=0.0.1
spring.ai.mcp.server.type=SYNC
spring.ai.mcp.server.resource-change-notification=true
spring.ai.mcp.server.tool-change-notification=true
spring.ai.mcp.server.prompt-change-notification=true

# 日志配置
logging.file.name=mcp-weather-stdio-server.log
logging.level.root=INFO
logging.level.donts.ai=DEBUG

# 天气API配置
weather.api.api-key=你的API密钥
```

### MCP客户端配置
windsurf支持的格式和Claude Desktop一样
```json
{
  "mcpServers": {
    "donts-ai-mcp-weather": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dlogging.pattern.console=",
        "-jar",
        "/yourRealPath/to/donts-ai-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

## 构建和运行

### 打包
打包后不需要手动运行
```bash
mvn clean package
```



## 使用说明

当Client需要调用的时候，LLM就可以通过MCP协议调用我们的工具：

1. **计算表达式**：
   - 工具名称：`calculate`
   - 参数：`{"expression": "3 + 4 * 2"}`

2. **查询城市天气**：
   - 工具名称：`getWeather`
   - 参数：`{"city": "昆明"}`

## 注意事项

1. demo使用了和风天气的API，调用查询服务前，需要在application.properties中配置和风天气API密钥,到[和风天气控制台](https://console.qweather.com/#/console)申请APIKey
,每天有免费的调用次数，用来做个demo已经够用了
2. 这个小demo参考了Spring [AI官方示例](https://github.com/spring-projects/spring-ai-examples/blob/main/model-context-protocol/weather/starter-stdio-server/README.md
   )
## 参考资料

- [Spring AI官方文档](https://docs.spring.io/spring-ai/reference/index.html)
- [Spring AI MCP示例](https://github.com/spring-projects/spring-ai-examples/blob/main/model-context-protocol/weather/starter-stdio-server/README.md)
- [和风天气API文档](https://dev.qweather.com/docs/)