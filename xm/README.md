# AI 天气预报 (AIWeather)

> 一个基于 Kotlin + Jetpack Compose 的智能天气预报应用，集成 DeepSeek AI 提供自然语言天气洞察、穿衣建议与活动推荐。

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue)
![Compose](https://img.shields.io/badge/Compose-BOM%202024.02-brightgreen)
![Material3](https://img.shields.io/badge/Material-3-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)

## GitHub 仓库

**仓库地址**：https://github.com/Areebol1/aiweather

## 目录

- [项目简介](#项目简介)
- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [架构设计](#架构设计)
- [快速开始](#快速开始)
- [安装指南](docs/INSTALL.md)
- [使用说明](docs/USAGE.md)
- [API 文档](docs/API.md)
- [与参考项目的差异化](#与参考项目的差异化)
- [开源许可证](#开源许可证)

## 项目简介

AIWeather 是一款现代化的 Android 天气预报应用，采用当前主流的 Kotlin + Jetpack Compose 技术栈构建。项目在保持与参考天气预报项目相似核心功能（城市查询、天气展示、温度切换、刷新）的同时，通过全新的技术选型与架构设计实现明显的技术差异性。

核心亮点是集成了 DeepSeek AI 大模型，将冰冷的数字天气数据转化为温暖的自然语言描述，为用户提供个性化的穿衣建议和活动推荐。

## 功能特性

### 核心天气功能
- **智能城市搜索**：输入城市名实时搜索，支持全球城市
- **当前天气展示**：温度、体感温度、天气状况、湿度、风速、气压、紫外线、降水概率
- **逐小时预报**：未来 24 小时天气趋势
- **7 日预报**：一周天气展望
- **温度单位切换**：摄氏度 / 华氏度一键切换
- **日出日落时间**：展示当日日出日落时刻

### AI 智能分析（差异化功能）
- **自然语言天气摘要**：AI 生成 30 字以内的天气总结
- **智能穿衣建议**：根据气温、天气状况推荐穿搭
- **户外活动推荐**：基于天气条件建议适宜活动

### 体验优化
- **动态天气主题**：根据当前天气（晴/阴/雨/雪/雷/夜）自动切换配色
- **搜索历史**：Room 数据库本地持久化搜索记录
- **收藏城市**：快速访问常用城市
- **Material 3 设计**：现代简约视觉风格
- **边缘到边缘**：沉浸式状态栏适配

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Kotlin | 1.9.22 |
| UI 框架 | Jetpack Compose | BOM 2024.02 |
| 设计规范 | Material 3 | - |
| 架构模式 | MVI (Model-View-Intent) | - |
| 依赖注入 | Hilt | 2.50 |
| 网络客户端 | Ktor Client | 2.3.7 |
| 序列化 | kotlinx.serialization | 1.6.2 |
| 异步 | Kotlin Coroutines + Flow | 1.7.3 |
| 本地存储 | Room | 2.6.1 |
| 图片加载 | Coil | 2.5.0 |
| 导航 | Navigation Compose | 2.7.7 |
| 编译 | KSP | 1.9.22-1.0.17 |
| 最低 SDK | Android 7.0 (API 24) | - |
| 目标 SDK | Android 14 (API 34) | - |

### 天气数据源
- **Open-Meteo API**（免费、无需 API Key）
  - 地理编码 API：`https://geocoding-api.open-meteo.com/v1/search`
  - 天气预报 API：`https://api.open-meteo.com/v1/forecast`

### AI 服务
- **DeepSeek API**
  - 端点：`https://api.deepseek.com/chat/completions`
  - 模型：`deepseek-chat`
  - 用途：生成自然语言天气描述、穿衣建议、活动推荐

## 架构设计

项目采用 **MVI (Model-View-Intent)** 单向数据流架构：

```
用户操作 → Intent → ViewModel → Repository → API/DB
                ↓                          ↓
             State ← ViewModel ← Repository
                ↓
             Compose UI 渲染
```

### 分层结构

```
com.ai.weather/
├── data/                    # 数据层
│   ├── local/               # Room 数据库
│   │   ├── Entities.kt      # 实体类
│   │   ├── Daos.kt          # 数据访问对象
│   │   └── WeatherDatabase.kt
│   ├── model/               # 数据模型
│   │   ├── GeocodingResult.kt
│   │   ├── WeatherForecastResponse.kt
│   │   ├── WeatherInfo.kt   # 领域模型
│   │   ├── WeatherCodeMapper.kt
│   │   └── DeepSeekModels.kt
│   ├── remote/              # 远程数据源
│   │   ├── ApiServices.kt   # Ktor API 服务
│   │   └── AiWeatherAnalyzer.kt
│   └── repository/
│       └── WeatherRepository.kt
├── di/                      # 依赖注入
│   ├── DatabaseModule.kt
│   └── NetworkModule.kt
├── ui/                      # 表现层
│   ├── components/          # 可复用组件
│   ├── screens/             # 屏幕
│   ├── state/               # MVI 状态管理
│   │   ├── WeatherContract.kt
│   │   └── WeatherViewModel.kt
│   └── theme/               # 主题
├── MainActivity.kt
└── WeatherApplication.kt    # Application 入口
```

## 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Kotlin 1.9.22

### 配置 API 密钥

1. 访问 [DeepSeek 平台](https://platform.deepseek.com/) 注册并获取 API Key
2. 编辑项目根目录的 `local.properties` 文件
3. 填入你的 API Key：

```properties
deepseek.api.key=sk-your-actual-api-key
```

> **说明**：即使不配置 DeepSeek API Key，应用的核心天气功能仍可正常使用，AI 分析功能会自动降级为本地规则生成的建议。

### 构建运行

```bash
# 克隆仓库
git clone https://github.com/Areebol1/aiweather.git

# 进入项目目录
cd aiweather

# 在 Android Studio 中打开项目，等待 Gradle 同步完成

# 连接 Android 设备或启动模拟器，点击 Run
```

详细安装步骤请参阅 [安装指南](docs/INSTALL.md)。

## 与参考项目的差异化

本项目在保持功能对等性的同时，通过以下方式实现技术差异性：

| 维度 | 参考项目 | 本项目 |
|------|---------|--------|
| 编程语言 | Java 17 | Kotlin 1.9.22 |
| UI 框架 | XML 布局 + View 系统 | Jetpack Compose 声明式 UI |
| 架构模式 | MVVM (ViewModel + LiveData) | MVI 单向数据流 |
| 网络框架 | Retrofit 2.9 + Gson | Ktor Client + kotlinx.serialization |
| 异步处理 | Thread + Handler + LiveData | Coroutines + Flow |
| 依赖注入 | 手动 new | Hilt |
| 本地存储 | 无 | Room 数据库 |
| 城市选择 | 15 个硬编码按钮 | 动态搜索 + 历史记录 |
| AI 集成 | 无 | DeepSeek AI 自然语言分析 |
| 主题 | 固定 XML 主题 | 根据天气动态切换主题 |
| 温度单位 | boolean isCelsius | 类型安全枚举 TempUnit |
| 错误处理 | try-catch 返回 Mock | Result sealed class |
| 数据模型 | Java POJO + setter | Kotlin data class 不可变 |

## 开源许可证

本项目基于 [MIT License](LICENSE) 开源。

## 致谢

- [Open-Meteo](https://open-meteo.com/) 提供免费天气 API
- [DeepSeek](https://www.deepseek.com/) 提供 AI 大模型服务
- [Jetpack Compose](https://developer.android.com/jetpack/compose) 现代 UI 工具包
