# 贡献指南

欢迎提交 Issue 和 PR。

## 提交前检查
请在本地确保以下命令通过：
```bash
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

## 提交流程
1. Fork 或基于主仓库创建分支。
2. 完成开发并补充必要文档。
3. 运行构建与测试。
4. 发起 PR，说明变更内容、原因和验证结果。

## 代码风格
- 统一使用 Kotlin。
- 新增业务逻辑优先使用 ViewModel + StateFlow。
- UI 使用 Compose 组件化组织。
- 公共类和函数需附带清晰 KDoc 注释。

## Commit Message 建议
- `feat: ...`
- `fix: ...`
- `refactor: ...`
- `docs: ...`
- `test: ...`

## 反馈建议
- Issue 中请附带复现步骤、预期结果、实际结果和运行环境。
