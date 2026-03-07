package com.example.sunnyweather.ui.place

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 地点搜索页面状态。
 */
data class PlaceUiState(
    val query: String = "",
    val places: List<Place> = emptyList(),
    val showBackground: Boolean = true
)

/**
 * 地点搜索页 ViewModel，管理查询状态与一次性事件。
 */
class PlaceViewModel : ViewModel() {

    /**
     * 页面持续状态。
     */
    private val _uiState = MutableStateFlow(PlaceUiState())

    /**
     * 对外只读页面状态。
     */
    val uiState: StateFlow<PlaceUiState> = _uiState.asStateFlow()

    /**
     * 一次性提示事件（如 Toast 文案）。
     */
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)

    /**
     * 对外只读事件流。
     */
    val events: SharedFlow<String> = _events.asSharedFlow()

    /**
     * 当前搜索请求序号，用于丢弃过期结果。
     */
    private var searchRequestId = 0L

    /**
     * 当前搜索任务，用于在输入变更时取消旧请求。
     */
    private var searchJob: Job? = null

    /**
     * 响应查询词变化；为空时清空结果，非空时触发搜索。
     */
    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.isBlank()) {
            searchJob?.cancel()
            _uiState.update { it.copy(places = emptyList(), showBackground = true) }
            return
        }
        searchPlaces(query)
    }

    /**
     * 执行地点搜索并更新页面状态。
     */
    private fun searchPlaces(query: String) {
        val requestId = ++searchRequestId
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { Repository.searchPlaces(query) }
            if (requestId != searchRequestId || query != _uiState.value.query) {
                return@launch
            }
            when (result) {
                is ApiResult.Success -> {
                    if (result.data.status == "ok") {
                        if (result.data.places.isNotEmpty()) {
                            _uiState.update {
                                it.copy(
                                    places = result.data.places,
                                    showBackground = false
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(places = emptyList(), showBackground = true)
                            }
                            _events.tryEmit("未能查询到任何地点")
                        }
                    } else {
                        _uiState.update {
                            it.copy(places = emptyList(), showBackground = true)
                        }
                        _events.tryEmit("查询地点失败，请稍后重试")
                    }
                }

                is ApiResult.Failure -> {
                    _uiState.update {
                        it.copy(places = emptyList(), showBackground = true)
                    }
                    _events.tryEmit(result.msg)
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(places = emptyList(), showBackground = true)
                    }
                    _events.tryEmit(result.exception.message ?: "未知错误")
                }
            }
        }
    }

    /**
     * 缓存用户选择的地点。
     */
    fun savePlace(place: Place) = Repository.savePlace(place)

    /**
     * 清理本地缓存地点。
     */
    fun clearSavedPlace() = Repository.clearSavedPlace()

    /**
     * 获取本地缓存地点。
     */
    fun getSavedPlace() = Repository.getSavedPlace()

    /**
     * 判断是否存在本地缓存地点。
     */
    fun isPlaceSaved() = Repository.isPlaceSaved()
}
