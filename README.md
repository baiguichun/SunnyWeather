# SunnyWeather

基于《第一行代码 Android》的天气应用复刻项目，当前版本已重构为 **Kotlin + Jetpack Compose**，并保留原有交互逻辑（地点搜索、天气详情、抽屉切换地点、下拉刷新）。

## 功能概览
- 地点搜索：输入关键词调用彩云天气地点接口。
- 地点缓存：自动保存用户选择地点并在下次启动快速进入天气页。
- 实时天气：展示当前温度、天气现象、空气质量指数。
- 未来预报：展示按天的天气和温度区间。
- 生活指数：感冒、穿衣、紫外线、洗车建议。
- 抽屉交互：在天气页左侧抽屉中继续搜索并切换地点。

## 技术栈
- Kotlin
- Jetpack Compose（Material 2 + Material 3）
- AndroidX ViewModel + StateFlow/SharedFlow
- Retrofit + Gson
- Coroutines

## 快速开始
### 1. 环境要求
- Android Studio（建议最新稳定版）
- JDK 17（AGP 8.4.1 要求）
- Android SDK（`compileSdk = 34`）

### 2. 克隆项目
```bash
git clone https://github.com/baiguichun/SunnyWeather.git
cd SunnyWeather
```

### 3. 配置本地 SDK
在项目根目录创建 `local.properties`：
```properties
sdk.dir=/Users/<your-name>/Library/Android/sdk
```
Windows 示例：
```properties
sdk.dir=C:\\Users\\<your-name>\\AppData\\Local\\Android\\Sdk
```

### 4. 运行应用
- 使用 Android Studio 直接运行 `app` 模块，或：
```bash
./gradlew :app:assembleDebug
```

## 常用命令
```bash
# 构建 Debug APK
./gradlew :app:assembleDebug

# 运行单元测试
./gradlew :app:testDebugUnitTest

# 运行静态检查
./gradlew :app:lintDebug
```

## 项目结构
```text
app/src/main/java/com/example/sunnyweather
├── MainActivity.kt                  # 地点搜索入口页
├── SunnyWeatherApplication.kt       # Application 与全局配置
├── logic
│   ├── Repository.kt                # 数据仓库
│   ├── dao/PlaceDao.kt              # 地点本地缓存
│   ├── model/                       # 网络/展示模型
│   └── network/                     # Retrofit 服务与结果封装
└── ui
    ├── place/                       # 地点搜索页面与状态
    └── weather/                     # 天气详情页面与状态
```

## 文档目录
- [架构设计](docs/ARCHITECTURE.md)
- [接口与数据说明](docs/API_AND_DATA.md)
- [开发指南](docs/DEVELOPMENT.md)
- [测试指南](docs/TESTING.md)
- [变更记录](docs/CHANGELOG.md)
- [贡献指南](CONTRIBUTING.md)

## 注意事项
- 当前接口 Token 位于 `SunnyWeatherApplication.TOKEN`，仅用于学习演示。
- 若用于生产环境，请改为安全的服务端签发或密钥管理方案。

## License
本项目用于学习与交流。
