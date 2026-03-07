# 测试指南

## 当前测试组成
- 单元测试：`app/src/test`
- 仪器测试：`app/src/androidTest`
- 静态检查：Android Lint

## 执行命令
```bash
# 单元测试
./gradlew :app:testDebugUnitTest

# 仪器测试（需连接设备或启动模拟器）
./gradlew :app:connectedDebugAndroidTest

# 静态检查
./gradlew :app:lintDebug

# 常用全量回归
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

## 手工回归清单
### 启动与缓存
- 首次安装后进入地点搜索页。
- 选择地点后重启应用可直接进入天气页。
- 当缓存损坏时不会崩溃，且可回到搜索页。

### 地点搜索
- 输入关键词可加载地点列表。
- 清空输入时恢复背景图和空列表状态。
- 网络失败时有 Toast 提示。

### 天气页
- 首次进入会自动拉取天气。
- 下拉刷新可重复拉取。
- 打开抽屉选择新地点后，标题和天气同时更新。
- 快速切换地点时不会被旧请求覆盖。

## 结果判定
- `assembleDebug` 成功。
- `testDebugUnitTest` 成功。
- `lintDebug` 无 error（warning 可按优先级逐步治理）。
