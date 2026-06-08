# Agent 开发规范文档

---

## 1. 项目概述

- **项目名称** ：校友直聘 (Alumni Direct)
- **项目类型** ：校友管理平台（校友端 + 企业端 + 管理端）
- **技术栈** ：Spring Boot 3.2 + Vue 3 + MySQL + Redis + SpringAI

---

## 2. 技术规范

### 2.1 后端技术栈

- **框架** ：Spring Boot 3.2.0
- **Java版本** ：JDK 17
- **ORM** ：MyBatis-Plus
- **数据库** ：MySQL 8.0
- **缓存** ：Redis
- **文档解析** ：Apache Tika 2.9.1
- **AI服务** ：GLM API、百度云OCR

### 2.2 前端技术栈

- **框架** ：Vue 3
- **构建工具** ：Vite
- **UI组件库** ：Element Plus
- **状态管理** ：Pinia
- **CSS预处理器** ：SCSS

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
│           ├── admin/             # 管理端页面
│           ├── company/           # 企业端页面
│           └── user/              # 校友端页面
│
├── alumni-direct-common/          # 公共模块
│   └── src/main/java/com/lightcs/
│       ├── config/                # 配置类
│       ├── enums/                 # 枚举类
│       ├── exception/             # 异常处理
│       ├── model/                 # 数据模型
│       │   ├── dto/               # 数据传输对象
│       │   ├── entity/            # 实体类
│       │   └── vo/                # 视图对象
│       ├── prompt/                # 提示词模板文件
│       ├── result/                # 统一响应封装
│       ├── service/               # 服务接口
│       │   └── impl/              # 服务实现
│       └── utils/                 # 工具类
│
├── alumni-direct-service/         # 业务服务模块
│   └── src/main/
│       ├── java/com/lightcs/
│       │   ├── component/         # 组件层（AI服务、OCR服务等）
│       │   │   ├── GlmApiService.java       # GLM API服务
│       │   │   ├── OcrService.java          # OCR服务接口
│       │   │   ├── ResumeParseService.java  # 简历解析服务
│       │   │   └── impl/                    # 组件实现
│       │   │       └── BaiduOcrServiceImpl.java
│       │   ├── config/            # 配置类
│       │   ├── controller/        # REST API控制器
│       │   ├── mapper/            # MyBatis Mapper接口
│       │   ├── model/             # 数据模型
│       │   │   ├── dto/           # 请求DTO
│       │   │   ├── pojo/          # 数据库实体
│       │   │   └── vo/            # 响应VO
│       │   ├── provider/          # 服务提供者
│       │   │   └── PromptTemplateService.java # 提示词模板服务
│       │   ├── service/           # 业务服务
│       │   │   └── impl/          # 业务服务实现
│       │   └── utils/             # 工具类
│       └── resources/
│           ├── mapper/            # MyBatis XML配置
│           ├── prompts/           # 提示词模板文件
│           │   └── resume-prompt.st
│           └── application.yml    # 应用配置
└── pom.xml                        # 父POM
```

---

## 4. 需求评估原则

### 4.1 需求合理性判断

在进行任何功能开发或修改之前，Agent 必须先进行需求合理性评估：

**评估维度：**

- **技术可行性**：评估需求是否在当前技术栈和架构下可实现
- **业务合理性**：评估需求是否符合业务逻辑和产品定位
- **资源投入**：评估实现需求所需的时间、人力和资源投入
- **风险评估**：评估需求可能带来的风险（如性能影响、安全隐患、兼容性问题）
- **优先级判断**：评估需求的紧急程度和重要性

### 4.2 不合理需求处理

如果评估后认为需求不合理，Agent 应：

1. **明确指出不合理之处**：清晰说明为什么需求不合理
2. **提供具体理由**：基于技术、业务或资源角度给出依据
3. **提供改进建议**：提出更合理的替代方案或优化建议
4. **保持专业态度**：以建设性的方式与用户沟通，避免直接否定

### 4.3 合理需求处理

如果评估后认为需求合理，Agent 应：

1. **提供详细方案**：包括技术选型、架构设计、实现步骤
2. **评估影响范围**：分析对现有系统的影响和潜在风险
3. **制定实施计划**：提供清晰的开发计划和时间预估

### 4.4 修改前必读原则

- **读取最新文件**：在进行任何代码修改之前，必须先读取相关文件的最新内容，确保基于最新代码进行修改
- **验证修改内容**：修改完成后，必须验证代码可以正常编译和运行

### 4.5 前端开发规范

- **检查命名冲突**：导入的函数名、变量名不能与组件内定义的同名，必要时使用 `as` 关键字重命名
- **编译验证**：完成前端功能改造后，必须检查代码是否存在语法错误、命名冲突等问题，确保项目可以正常运行

---

## 5. 命名规范

### 5.1 Java 命名规范

| 类型  | 规范               | 示例                                |
|-----|------------------|-----------------------------------|
| 类名  | UpperCamelCase   | `UserController`, `ResumeDTO`     |
| 方法名 | lowerCamelCase   | `getUserById`, `parseResume`      |
| 变量名 | lowerCamelCase   | `userName`, `pageSize`            |
| 常量  | UPPER_SNAKE_CASE | `MAX_PAGE_SIZE`, `DEFAULT_STATUS` |
| 包名  | 全小写              | `com.lightcs.service`             |
| 枚举值 | UPPER_SNAKE_CASE | `USER_ROLE_ADMIN`                 |

### 5.2 前端命名规范

| 类型    | 规范               | 示例                                   |
|-------|------------------|--------------------------------------|
| 组件名   | PascalCase       | `UserProfile.vue`, `JobFairCard.vue` |
| 方法/变量 | camelCase        | `userName`, `handleClick`            |
| 样式类   | kebab-case       | `user-card`, `job-fair-list`         |
| 常量    | UPPER_SNAKE_CASE | `API_BASE_URL`                       |

---

## 6. 代码规范

### 6.1 Controller 层

- 负责接收请求和返回响应
- 使用 `@RestController` 注解
- 返回统一响应格式 `BaseResponse`
- 抛出异常使用`ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "更新审批记录失败");`

### 6.2 Service 层

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
- 判断后抛出异常可参考`ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "更新审批记录失败");`
- 异常码可参考`ErrorCode`枚举类
- Redis的常量KEY统一放在`RedisConstants`类中

### 6.3 Entity/VO/DTO

- **Entity** ：数据库表对应的实体，使用 `@TableName` 注解
- **VO** ：视图对象，用于返回给前端
- **DTO** ：数据传输对象，用于接收前端参数
- 字段存在多个枚举值时，赋值时使用枚举值，枚举类放在enums包中

### 6.4 统一响应

- 成功：`ResultBuilder.success(data)`
- 失败：`ResultBuilder.error(ErrorCode)`

### 6.5 AI 组件层规范（GlmApiService）

**位置** ：`component/` 目录下

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

## 7. 提示词管理规范

### 7.1 提示词文件位置

- 提示词模板统一存放在 `src/main/resources/prompts/` 目录下
- 禁止在代码中硬编码提示词

### 7.2 提示词文件格式

- 使用 `.txt` 或 `.json` 格式存储
- 文件名格式：`{业务场景}_{功能}.txt`
- 示例：`resume-parse.txt`, `job-recommend.txt`

### 7.3 提示词相关服务

- 使用 `com.lightcs.provider.PromptTemplateService` 提供提示词加载和管理功能
- 支持占位符替换（如 `${name}`, `${context}`）

### 7.4 目录结构

```
src/main/resources/
└── prompts/
    ├── resume_parse.txt           # 简历解析提示词
    ├── job_recommend.txt          # 职位推荐提示词
    └── interview_qa.txt           # 面试问答提示词
```

---

## 8. 数据库规范

### 8.1 表设计

- 使用 InnoDB 存储引擎
- 主键使用 `varchar(36)` 或自增主键
- 表名和字段名使用 snake_case
- 添加必要的索引优化查询性能

### 8.2 SQL 编写

- 使用 MyBatis-Plus 提供的 CRUD 方法
- 复杂查询使用 XML 或注解方式编写
- 避免 SQL 注入风险

### 8.3 建表语句管理

- 所有建表语句应放在 `src/main/resources/sql/` 目录下
- 新增表或字段时，应追加到现有 SQL 文件或创建新文件

---

## 9. 前端开发规范

### 9.1 组件设计

- 组件职责单一，遵循单一职责原则
- 合理划分组件粒度，避免过大或过小
- 使用 props 传递数据，使用 emit 触发事件

### 9.2 状态管理

- 使用 Pinia 进行全局状态管理
- 避免过度使用全局状态，优先使用组件局部状态
- 状态更新应通过 action 方法进行

### 9.3 API 调用

- API 请求统一封装在 `src/api/` 目录下
- 使用 Axios 拦截器统一处理请求和响应
- 添加请求超时和错误处理机制

---

## 10. 安全规范

### 10.1 输入验证

- 所有用户输入必须进行验证
- 使用参数校验注解（如 `@Valid`, `@NotBlank`）
- 对文件上传进行类型和大小限制

### 10.2 敏感数据保护

- 密码等敏感信息应加密存储
- 避免在日志中打印敏感数据
- API 响应中不应返回敏感字段

### 10.3 权限控制

- 使用 Spring Security 进行权限管理
- 基于角色的访问控制（RBAC）
- 对敏感接口进行权限校验

---

## 11. 文档规范

### 11.1 API 文档

- 使用 Swagger/OpenAPI 生成 API 文档
- 每个接口应包含清晰的请求参数和响应示例
- 及时更新文档以反映代码变更

### 11.2 代码注释

- 为复杂逻辑添加必要的注释
- 使用 JavaDoc 风格编写方法注释
- 注释应说明"为什么"而非"做什么"

---

## 12. 测试规范

### 12.1 单元测试

- 对 Service 层进行单元测试
- 使用 Mockito 模拟依赖
- 覆盖主要业务场景

### 12.2 集成测试

- 测试完整的业务流程
- 验证 API 接口的正确性
- 测试边界条件和异常情况

---

## 13. 协作规范

### 13.1 分支管理

- 使用 Git Flow 工作流
- 功能开发在 feature 分支进行
- 代码审查通过后合并到 develop 分支

### 13.2 代码审查

- 所有代码提交前必须进行代码审查
- 审查重点包括：代码质量、安全性、性能、可维护性
- 使用统一的审查 checklist

### 13.3 提交规范

- 提交信息应清晰描述变更内容
- 使用约定式提交格式（Conventional Commits）
- 每次提交应只包含一个逻辑变更

---

**版本**: 1.1  
**创建日期**: 2026-06-08  
**适用项目**: alumni-direct