# Agent 开发规范

## 1. 项目概述

- **项目名称**：校友直聘 (Alumni Direct)
- **项目类型**：校友管理平台（校友端 + 企业端 + 管理端）
- **技术栈**：Spring Boot 3.2 + Vue 3 + MySQL + Redis + SpringAI

---

## 2. 技术规范

### 2.1 后端技术栈

- **框架**：Spring Boot 3.2.0
- **Java版本**：JDK 17
- **ORM**：MyBatis-Plus
- **数据库**：MySQL 8.0
- **缓存**：Redis
- **文档解析**：Apache Tika 2.9.1
- **AI服务**：GLM API、百度云OCR

### 2.2 前端技术栈

- **框架**：Vue 3
- **构建工具**：Vite
- **UI组件库**：Element Plus
- **状态管理**：Pinia
- **CSS预处理器**：SCSS

---

## 3. 项目结构

```
alumni-direct/
├── alumni-direct-ui/              # 前端项目
│   └── src/
│       ├── api/                   # API 接口定义
│       ├── components/            # 公共组件
│       ├── router/                # 路由配置
│       ├── stores/                # Pinia 状态管理
│       └── views/                 # 页面视图
│           ├── admin/            # 管理端页面
│           ├── company/          # 企业端页面
│           └── user/              # 校友端页面
│
├── alumni-direct-common/          # 公共模块
│   └── src/main/java/com/lightcs/
│       ├── config/               # 配置类
│       ├── enums/                # 枚举类
│       ├── exception/            # 异常处理
│       ├── model/                # 数据模型
│       │   ├── dto/              # 数据传输对象
│       │   ├── entity/           # 实体类
│       │   └── vo/               # 视图对象
│       ├── prompt/               # 提示词模板文件
│       ├── result/               # 统一响应封装
│       ├── service/              # 服务接口
│       │   └── impl/             # 服务实现
│       └── utils/                # 工具类
│
├── alumni-direct-service/         # 业务服务模块
│   └── src/main/
│       ├── java/com/lightcs/
│       │   ├── component/         # 组件层（AI服务、OCR服务等）
│       │   │   ├── GlmApiService.java         # GLM API服务
│       │   │   ├── OcrService.java            # OCR服务接口
│       │   │   ├── ResumeParseService.java    # 简历解析服务
│       │   │   └── impl/                      # 组件实现
│       │   │       └── BaiduOcrServiceImpl.java
│       │   ├── config/           # 配置类
│       │   ├── controller/       # REST API控制器
│       │   ├── mapper/           # MyBatis Mapper接口
│       │   ├── model/            # 数据模型
│       │   │   ├── dto/          # 请求DTO
│       │   │   ├── pojo/         # 数据库实体
│       │   │   └── vo/           # 响应VO
│       │   ├── provider/         # 服务提供者
│       │   │   └── PromptTemplateService.java # 提示词模板服务
│       │   ├── service/          # 业务服务
│       │   │   └── impl/         # 业务服务实现
│       │   └── utils/            # 工具类
│       └── resources/
│           ├── mapper/           # MyBatis XML配置
│           ├── prompts/          # 提示词模板文件
│           │   └── resume-prompt.st
│           └── application.yml   # 应用配置
└── pom.xml                       # 父POM
```

---

## 4. 命名规范

### 4.1 Java 命名规范

| 类型  | 规范               | 示例                                |
|-----|------------------|-----------------------------------|
| 类名  | UpperCamelCase   | `UserController`, `ResumeDTO`     |
| 方法名 | lowerCamelCase   | `getUserById`, `parseResume`      |
| 变量名 | lowerCamelCase   | `userName`, `pageSize`            |
| 常量  | UPPER_SNAKE_CASE | `MAX_PAGE_SIZE`, `DEFAULT_STATUS` |
| 包名  | 全小写              | `com.lightcs.service`             |
| 枚举值 | UPPER_SNAKE_CASE | `USER_ROLE_ADMIN`                 |

### 4.2 前端命名规范

| 类型    | 规范               | 示例                                   |
|-------|------------------|--------------------------------------|
| 组件名   | PascalCase       | `UserProfile.vue`, `JobFairCard.vue` |
| 方法/变量 | camelCase        | `userName`, `handleClick`            |
| 样式类   | kebab-case       | `user-card`, `job-fair-list`         |
| 常量    | UPPER_SNAKE_CASE | `API_BASE_URL`                       |

---

## 5. 代码规范

### 5.1 Controller 层

- 负责接收请求和返回响应
- 使用 `@RestController` 注解
- 参数校验使用 `@Valid` 注解
- 返回统一响应格式 `BaseResponse`

### 5.2 Service 层

**职责**：

- 业务逻辑处理
- 事务管理
- 调用多个 DAO 进行数据操作

**结构**：

- `service/`：存放服务接口（如 `UserService.java`）
- `service/impl/`：存放服务实现类（如 `UserServiceImpl.java`）

**规范**：

- 接口命名：`{业务模块}Service`
- 实现类命名：`{业务模块}ServiceImpl`
- 使用 `@Service` 注解标识
- 使用 `@Transactional` 管理事务
- 业务异常抛出 `BusinessException`

### 5.3 Entity/VO/DTO

- **Entity**：数据库表对应的实体，使用 `@TableName` 注解
- **VO**：视图对象，用于返回给前端
- **DTO**：数据传输对象，用于接收前端参数

### 5.4 统一响应

- 成功：`ResultBuilder.success(data)`
- 失败：`ResultBuilder.error(ErrorCode)`

### 5.5 AI 组件层规范（GlmApiService）

**位置**：`component/` 目录下

**职责**：

- 封装 AI 模型调用逻辑
- 提供同步/异步/流式调用方式
- 与提示词模板服务集成

**设计模式参考（AiInterviewController）**：

| API 类型 | 端点示例           | 用途          |
|--------|----------------|-------------|
| 同步调用   | `/ai`          | 普通问答，返回完整结果 |
| 流式调用   | `/ai/stream`   | 大文本响应，实时推送  |
| 模型直调   | `/call/chat`   | 直接调用 GLM 模型 |
| 流式直调   | `/stream/chat` | 流式调用 GLM 模型 |

**规范要求**：

- 使用 `Spring AI` 的 `ChatClient` 进行封装
- 提示词通过 `PromptTemplateService` 获取，禁止硬编码
- 支持多种模型选择（如 `glm-4.6`）
- 支持温度参数配置
- 流式响应使用 `ServerSentEvent`

---

## 6. 提示词管理规范

### 6.1 提示词文件位置

- 提示词模板统一存放在 `src/main/resources/prompt/` 目录下
- 禁止在代码中硬编码提示词

### 6.2 提示词文件格式

- 使用 `.txt` 或 `.json` 格式存储
- 文件名格式：`{业务场景}_{功能}.txt`
- 示例：`resume_parse.txt`, `job_recommend.txt`

### 6.3 提示词相关服务

- 使用 `com.lightcs.provider.PromptTemplateService` 提供提示词加载和管理功能
- 支持占位符替换（如 `${name}`, `${context}`）

### 6.4 目录结构

```
src/main/resources/
└── prompt/
    ├── resume_parse.txt          # 简历解析提示词
    ├── job_recommend.txt         # 职位推荐提示词
    └── interview_qa.txt          # 面试问答提示词
