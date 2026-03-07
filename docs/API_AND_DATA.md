# 接口与数据说明

## 基础信息
- Base URL：`https://api.caiyunapp.com/`
- Token 来源：`SunnyWeatherApplication.TOKEN`
- 网络栈：Retrofit + Gson

## 接口列表
### 1) 地点搜索
- 方法：`GET`
- 路径：`v2/place?token=<TOKEN>&lang=zh_CN`
- Query：`query`（搜索关键词）
- 对应接口：`PlaceService.searchPlaces()`

### 2) 实时天气
- 方法：`GET`
- 路径：`v2.5/<TOKEN>/{lng},{lat}/realtime.json`
- Path：`lng`、`lat`
- 对应接口：`WeatherService.getRealtimeWeather()`

### 3) 未来天气
- 方法：`GET`
- 路径：`v2.5/<TOKEN>/{lng},{lat}/daily.json`
- Path：`lng`、`lat`
- 对应接口：`WeatherService.getDailyWeather()`

## 核心模型
### 地点相关
- `PlaceResponse(status, places)`
- `Place(name, location, address)`
- `Location(lng, lat)`

### 天气相关
- `RealtimeResponse`：实时天气、AQI。
- `DailyResponse`：预报温度、天气现象、生活指数。
- `Weather`：将 `RealtimeResponse.Realtime` 和 `DailyResponse.Daily` 聚合后的展示模型。

### 天气资源映射
- `Sky(info, icon, bg)`：天气码到中文文案、图标、背景图的映射。
- `getSky(skycon)`：未知码默认回退到 `CLEAR_DAY`。

## 网络结果封装
使用 `ApiResult<T>` 统一状态：
- `Success<T>(data)`
- `Failure(code, msg)`
- `Error(exception)`

`filterResponse()` 负责：
- 解析 HTTP 成功与失败。
- 响应体为空时返回 `Failure(-1000, ...)`。
- 透传协程取消异常（`CancellationException`）。
- 其他异常返回 `Error`。

## 本地缓存
- 存储位置：`SharedPreferences("sunny_weather")`
- Key：`place`
- 序列化方式：Gson
- 读取失败时返回 `null`，上层可执行缓存清理。
