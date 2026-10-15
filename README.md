# Mazewu AI 应用生成平台

> **Mazewu** · [github.com/Mazewu](https://github.com/Mazewu)

一个 AI 驱动的零代码应用生成平台。用户用自然语言描述需求，平台自动完成技术选型、生成完整可运行的项目代码，并提供**实时预览、可视化编辑、一键部署分享、源码打包下载**的完整闭环。

项目基于 **Spring Boot 3 + LangChain4j + LangGraph4j + Vue 3** 构建，覆盖 AI 智能体、AI 工作流、工具调用、流式输出、多级缓存、接口限流、可观测性等企业级实践。

---

## 一、核心能力

| 能力 | 说明 |
| --- | --- |
| **智能代码生成** | 输入需求描述，AI 自动分析并路由到最合适的生成策略：单文件 HTML / 多文件工程 / Vue 3 完整项目 |
| **流式可视反馈** | SSE 实时推送 AI 的推理过程、工具调用与执行结果，生成过程全程可见 |
| **可视化编辑** | 生成结果即时预览，可进入编辑模式直接选中页面元素，与 AI 对话式修改，直到满意 |
| **一键部署分享** | 生成的应用一键部署到云端、自动截取封面图，产出可访问地址用于分享 |
| **源码下载** | 支持将生成的项目完整打包下载 |
| **企业级管理** | 用户管理、应用管理、精选应用设置、AI 调用量监控、系统性能监控 |

---

## 二、技术栈

### 后端

| 分类 | 技术 |
| --- | --- |
| 语言 / 运行时 | Java 21 |
| 应用框架 | Spring Boot 3.5 |
| AI 编排 | LangChain4j、LangGraph4j（AI 工作流与多智能体编排） |
| 数据访问 | MyBatis-Plus、MySQL 8 |
| 缓存 / 会话 | Redis、Caffeine（本地 + 分布式多级缓存） |
| 认证授权 | Spring Session + Redis、自定义 `@AuthCheck` 注解 + AOP 拦截 |
| 对象存储 | 腾讯云 COS |
| 接口文档 | Knife4j / SpringDoc OpenAPI |
| 可观测性 | Spring Boot Actuator + Micrometer + Prometheus + Grafana |

### 前端

| 分类 | 技术 |
| --- | --- |
| 框架 | Vue 3 + TypeScript + Vite |
| 状态管理 | Pinia |
| UI 组件库 | Ant Design Vue |
| 路由 | Vue Router |
| 请求 | Axios |

---

## 三、架构要点

### 1. AI 代码生成链路

```
用户需求
  └─> 智能路由（AiCodeGenTypeRoutingService）  判定生成类型：HTML / 多文件 / Vue 工程
        └─> 门面统一调度（AiCodeGeneratorFacade）
              └─> 流式对话（StreamingChatModel）
                    └─> 工具调用（文件读写 / 修改 / 删除 / 目录读取）
                          └─> 代码解析（CodeParser）
                                └─> 文件落盘（CodeFileSaver）
                                      └─> 项目构建（VueProjectBuilder）
```

### 2. AI 工作流（LangGraph4j）

除直线链路外，项目还实现了一套基于 LangGraph4j 的多节点工作流，用于处理更复杂的生成任务：

- `PromptEnhancerNode` — 提示词增强
- `RouterNode` — 任务路由
- `CodeGeneratorNode` — 代码生成
- `CodeQualityCheckNode` — 生成质量校验
- `ImagePlanNode` + 并发图片采集节点（Logo / 插图 / 图表 / 内容图）— 素材并发采集
- `ProjectBuilderNode` — 项目构建

同时提供了并发工作流、子图工作流等编排形态，便于按业务复杂度选择。

### 3. 设计模式应用

- **工厂模式**：`AiCodeGeneratorServiceFactory`、`CodeParserExecutor` 等按生成类型动态产出实例
- **策略 + 模板方法**：`CodeFileSaverTemplate` 定义落盘骨架，子类实现差异部分
- **执行器模式**：解析器 / 保存器统一由 Executor 分发，新增类型无需改动调用方
- **AOP**：鉴权、限流、缓存统一以注解 + 切面实现

---

## 四、快速开始

### 环境要求

| 依赖 | 版本 |
| --- | --- |
| JDK | 21+ |
| Maven | 3.9+ |
| MySQL | 8.0+ |
| Redis | 6+ |
| Node.js | 18+ |

### 1. 初始化数据库

```bash
mysql -u root -p < sql/create_table.sql
```

### 2. 配置后端

`src/main/resources/application.yml` 中所有需要你自行填写的项都标注为 `YOUR_*`，全局搜索 `YOUR_` 即可定位：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mazewu_ai_code
    username: root
    password: YOUR_MYSQL_PASSWORD

langchain4j:
  open-ai:
    streaming-chat-model:
      base-url: https://api.deepseek.com
      api-key: YOUR_DEEPSEEK_API_KEY

cos:
  client:
    secretId: YOUR_COS_SECRET_ID
    secretKey: YOUR_COS_SECRET_KEY
    region: ap-shanghai
    bucket: YOUR_COS_BUCKET

pexels:
  api-key: YOUR_PEXELS_API_KEY

dashscope:
  api-key: YOUR_DASHSCOPE_API_KEY
```

### 3. 启动后端

```bash
mvn spring-boot:run
```

服务运行在 `http://localhost:8123/api`，接口文档：`http://localhost:8123/api/doc.html`

### 4. 启动前端

```bash
cd mazewu-ai-code-frontend
npm install
npm run dev
```

---

## 五、目录结构

```
mazewu-ai-code
├── sql/                                  # 数据库初始化脚本
├── prometheus.yml                        # 监控抓取配置
├── grafana/                              # Grafana 看板配置
├── tools/                                # 项目改造留档脚本（确认无误后可删）
├── src/main/java/com/mazewu/aicode/
│   ├── ai/                               # AI 服务、门面、工具调用、护栏
│   │   ├── guardrail/                     # 输入安全 / 输出重试护栏
│   │   ├── model/                         # 结构化生成结果模型
│   │   └── tools/                         # AI 可调用的文件操作工具集
│   ├── aop/                              # 鉴权切面
│   ├── common/                           # 通用响应、分页、结果工具
│   ├── config/                           # 模型、缓存、CORS、对象存储等配置
│   ├── constant/                         # 常量
│   ├── controller/                       # 接口层
│   ├── core/                             # 生成核心：解析器、保存器、流式处理器
│   ├── exception/                        # 业务异常与全局异常处理
│   ├── generator/                        # 代码生成器
│   ├── langgraph4j/                       # AI 工作流：节点、子图、并发编排
│   ├── manager/                          # 外部服务封装
│   ├── mapper/                           # MyBatis Mapper
│   ├── model/                            # 实体、DTO、VO、枚举
│   ├── monitor/                          # AI 模型调用监控
│   ├── ratelimter/                       # 接口限流
│   ├── service/                          # 业务服务
│   └── utils/                            # 工具类
└── mazewu-ai-code-frontend/               # Vue 3 前端
```

---

## 六、说明

- 本仓库为个人学习与技术实践作品，用于展示 AI 应用开发与后端架构设计能力。
- 项目中的密钥、服务器地址均为占位符，不包含任何真实凭据。
- 若你希望基于本项目二次开发，请自行申请对应的 AI 模型与云存储服务。

---

## License

MIT
