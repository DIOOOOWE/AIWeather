# 安装指南

## 系统要求

### 开发环境
- **操作系统**：Windows 10/11、macOS 10.15+、Linux
- **IDE**：Android Studio Hedgehog (2023.1.1) 或更高版本
- **JDK**：17 或更高版本
- **Android SDK**：API Level 34（Android 14）

### 运行环境
- **最低 Android 版本**：Android 7.0（API 24）
- **目标 Android 版本**：Android 14（API 34）
- **推荐设备**：Android 8.0 及以上设备以获得最佳体验

## 详细安装步骤

### 步骤 1：安装 Android Studio

1. 访问 [Android Studio 官网](https://developer.android.com/studio) 下载最新版本
2. 运行安装程序，按向导完成安装
3. 启动 Android Studio，等待初始化和 SDK 下载完成

### 步骤 2：克隆项目

```bash
git clone https://github.com/Areebol1/aiweather.git
cd aiweather
```

或使用 Android Studio 直接从版本控制导入：
1. 启动 Android Studio
2. 选择 "Get from VCS"
3. 输入仓库 URL：`https://github.com/Areebol1/aiweather.git`
4. 选择本地存储路径，点击 Clone

### 步骤 3：配置 API 密钥（可选但推荐）

本项目的 AI 分析功能依赖 DeepSeek API。获取步骤：

1. 访问 [DeepSeek 开放平台](https://platform.deepseek.com/)
2. 注册账号并登录
3. 进入 API Keys 管理页面
4. 创建新的 API Key
5. 在项目根目录的 `local.properties` 文件中填入：

```properties
deepseek.api.key=sk-你的实际API密钥
```

> **注意**：`local.properties` 文件已在 `.gitignore` 中，不会被提交到版本库。
>
> **降级方案**：如不配置 API Key，AI 分析功能会自动使用本地规则生成建议，核心天气功能不受影响。

### 步骤 4：Gradle 同步

1. 在 Android Studio 中打开项目
2. 等待 Gradle 自动同步完成（首次同步需下载依赖，可能需要几分钟）
3. 如同步失败，检查：
   - 网络连接是否正常
   - JDK 版本是否为 17
   - Android SDK 34 是否已安装

### 步骤 5：连接设备

**方式一：使用模拟器**
1. 在 Android Studio 中打开 AVD Manager
2. 创建虚拟设备（推荐 Pixel 6，API 34）
3. 启动模拟器

**方式二：使用真机**
1. 在手机上启用 "开发者选项"
2. 开启 "USB 调试"
3. 用 USB 线连接电脑
4. 在手机弹窗中允许 USB 调试

### 步骤 6：构建与运行

1. 在 Android Studio 工具栏选择目标设备
2. 点击 Run（绿色三角形）按钮
3. 等待编译完成，应用将自动安装并启动

## 常见问题

### Q1：Gradle 同步失败 "Could not resolve all files"

**解决方案**：
- 检查网络连接
- 在 `settings.gradle.kts` 中确认仓库配置包含 `google()` 和 `mavenCentral()`
- 尝试在 `gradle.properties` 中添加代理配置

### Q2：编译错误 "Unresolved reference: BuildConfig"

**解决方案**：
确认 `app/build.gradle.kts` 中已启用 `buildConfig = true`：
```kotlin
buildFeatures {
    compose = true
    buildConfig = true
}
```

### Q3：运行时崩溃 "Hilt dependencies not generated"

**解决方案**：
- 执行 Build > Clean Project
- 执行 Build > Rebuild Project
- 确认 KSP 插件已正确配置

### Q4：AI 分析功能无响应

**可能原因**：
- 未配置 DeepSeek API Key
- API Key 无效
- 网络连接问题

**解决方案**：
- 检查 `local.properties` 中的 API Key
- 查看 Logcat 日志排查网络错误
- 应用会自动降级为本地规则生成建议

## 卸载

- 在设备上长按应用图标，选择卸载
- 或执行命令：`adb uninstall com.ai.weather`
