# Changelog

## Unreleased
### Added
- 补充完整项目文档：`README`、架构、接口数据、开发、测试与贡献说明。

## 2026-03-07
### Changed
- UI 从 XML/Fragment 重构为 Compose。
- 交互保持一致：地点搜索、抽屉切换地点、下拉刷新。
- ViewModel 改为 `StateFlow + SharedFlow`。

### Fixed
- 修复缓存地点反序列化失败导致的空指针风险。
- 修复天气并发请求的结果覆盖问题（过期请求丢弃）。
- 修复网络层吞掉协程取消异常的问题（保留取消语义）。

### Docs
- 全量补充类与函数 KDoc。
