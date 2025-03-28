# MCP 服务器演示项目

这是一个基于Spring Boot的Model Context Protocol (MCP) 服务器演示项目，提供了计算器、天气查询以及数据模型和API接口工具的功能。

## 项目概述

本项目是一个简单的MCP服务器实现，使用Spring Boot和WebFlux构建，提供了以下功能：

1. **计算器工具** - 可以计算数学表达式
2. **天气查询工具** - 可以查询指定城市的实时天气
3. **系统信息资源** - 提供系统基础信息
4. **数据模型查找工具** - 根据用户描述查找匹配的数据模型
5. **API接口查找工具** - 根据数据模型查找对应的API接口
6. **API执行工具** - 根据用户输入构造参数并调用实际接口

## 技术栈

- Java 17
- Spring Boot
- Spring WebFlux
- Model Context Protocol (MCP)
- Server-Sent Events (SSE)

## 快速开始

### 前置条件

- JDK 17 或更高版本
- Maven 3.6 或更高版本
- 和风天气API密钥（用于天气查询功能）

### 配置

1. 克隆项目到本地
2. 在 `src/main/resources/application.yml` 中配置和风天气API密钥

### 构建与运行

```bash
# 编译项目
mvn clean package

# 运行项目
java -jar target/donts-ai-0.0.1-SNAPSHOT.jar
```

## API 端点

### SSE 连接

- **端点**: `/sse`
- **方法**: GET
- **描述**: 建立SSE连接，用于接收服务器发送的事件

### 消息发送

- **端点**: `/mcp/messages`
- **方法**: POST
- **描述**: 发送消息到MCP服务器

## 工具功能

### 计算器工具

计算器工具可以处理各种数学表达式，包括：
- 基本运算（加减乘除）
- 复杂表达式计算

示例：
```
2 + 2
(3 * 4) / 2
sqrt(16) + 5
```

### 天气查询工具

天气查询工具可以获取指定城市的实时天气信息，包括：
- 温度
- 天气状况
- 风向风速
- 湿度等

示例：
```
北京
shanghai
guangzhou
```

### 数据模型和API工具

项目新增了三个相互关联的工具，用于模拟数据模型查找、API接口查找和API执行的流程：

#### 数据模型查找工具

根据用户的描述查找匹配的数据模型，支持以下功能：
- 关键词匹配和相似度计算
- 返回匹配度评分
- 支持详细信息查看

示例：
```
{
  "description": "用户账号",
  "maxResults": 3,
  "includeDetails": true
}
```

#### API接口查找工具

根据数据模型查找对应的API接口，支持以下功能：
- 按操作类型（查询、创建、更新、删除）过滤
- 返回匹配度评分
- 支持详细参数信息查看

示例：
```
{
  "modelId": "model_user",
  "operationType": "查询",
  "maxResults": 5,
  "includeDetails": true
}
```

#### API执行工具

根据用户输入构造参数并调用实际接口，支持以下功能：
- 参数验证和类型转换
- 模拟API调用并生成响应
- 支持详细元数据输出

示例：
```
{
  "apiId": "api_user_get",
  "parameters": {"userId": "u123456"},
  "validateParameters": true,
  "detailedResponse": true
}
```

### 模拟数据案例

系统预置了三种业务场景的模拟数据：

1. **用户模型** - 包含用户基本信息及相关API
2. **订单模型** - 包含订单信息及相关API
3. **商品模型** - 包含商品信息及相关API

## 系统架构

项目基于Spring Boot和Model Context Protocol构建，使用Server-Sent Events (SSE) 作为通信机制。主要组件包括：

1. **MCP服务器** - 处理客户端请求并返回响应
2. **工具实现** - 提供具体功能的实现
3. **资源配置** - 定义系统资源
4. **模拟数据仓库** - 存储和检索模拟数据
5. **服务实现** - 实现业务逻辑

## 贡献指南

欢迎提交问题和改进建议！请遵循以下步骤：

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开一个 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详情请参阅 LICENSE 文件