package com.example.librarymanager

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class TestActivity : AppCompatActivity() {

    private val JSON = "application/json; charset=utf-8".toMediaType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            // 创建 OkHttpClient 实例
            val client = OkHttpClient()

            // 创建一个 GET 请求
            val request = Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts/1")
                .build()

            // 使用 client 发送请求并获取响应
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("bixiaofu", "onFailure")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        // 读取响应体
                        val responseBody = response.body?.string()
                        Log.e("bixiaofu", "Response: $responseBody")
                    } else {
                        Log.e("bixiaofu", "Request failed: ${response.code}")
                    }
                }
            })
        }
        return super.onTouchEvent(event)
    }
}