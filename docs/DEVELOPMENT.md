# 开发指南

## 分支与提交
当前仓库默认主分支为 `main`。

建议提交信息格式：
- `feat: ...`
- `fix: ...`
- `refactor: ...`
- `docs: ...`

## 代码组织约定
- `ui/` 仅处理渲染和交互，不直接访问 Retrofit。
- `ViewModel` 只通过 `Repository` 访问数据。
- 网络与缓存逻辑分别放在 `logic/network` 和 `logic/dao`。
- 展示状态优先使用 `StateFlow`；一次性事件使用 `SharedFlow`。

## 新增功能建议步骤
1. 在 `model` 定义响应模型。
2. 在 `network` 增加 Service 方法。
3. 在 `Repository` 暴露统一入口。
4. 在对应 ViewModel 添加状态与交互函数。
5. 在 Compose 页面中消费状态并处理事件。

## 常见开发任务
### 更新依赖
1. 修改 `gradle/libs.versions.toml`。
2. 执行：
```bash
./gradlew :app:assembleDebug
```

### 添加新页面
1. 新建 `ui/<feature>/<Feature>Screen.kt`。
2. 新建 `<Feature>ViewModel.kt`。
3. 在 Activity 中 `setContent` 绑定路由函数。

### 排查网络问题
- 检查 `ServiceCreator.BASE_URL`。
- 检查 Token 是否有效。
- 查看 `NetworkUtil.kt` 日志输出。

## 代码质量检查
每次改动建议至少执行：
```bash
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```
