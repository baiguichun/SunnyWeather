package com.example.sunnyweather

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android 设备端示例测试。
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    /**
     * 验证被测应用包名。
     */
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.sunnyweather", appContext.packageName)
    }
}
