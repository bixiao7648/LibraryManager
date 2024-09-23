package com.example.librarymanager

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.librarymanager.speech.SpeechViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class TestActivity : AppCompatActivity() {

    companion object {
        private const val API_KEY = "sk-rZ4onUGzPmD68bpbY3xr9tbaIiWvJgYP2YqTVoRUZa9T3I33"
        private const val BASE_URL = "https://api.fe8.cn/v1"
    }

    private lateinit var etTestCondition: EditText
    private lateinit var etTestInput: EditText
    private lateinit var btTestSend: Button
    private lateinit var tvTestResult: TextView
    private val speechViewModel: SpeechViewModel by lazy {
        ViewModelProvider(this)[SpeechViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        etTestCondition = findViewById(R.id.etTestCondition)
        etTestInput = findViewById(R.id.etTestInput)
        tvTestResult = findViewById(R.id.tvTestResult)
        btTestSend = findViewById(R.id.btTestSend)
        btTestSend.setOnClickListener { send() }
        lifecycleScope.launch {
            speechViewModel.speechContent.collectLatest {
                currentFocus?.let { view ->
                    if (view is EditText) {
                        view.setText(it)
                    }
                }
            }
        }
    }

    private fun send() {
        lifecycleScope.launch(Dispatchers.IO) {
            // 创建 OkHttpClient 实例
            val client = OkHttpClient()

            // 构建消息
            val messages = JSONArray()
                .put(JSONObject().apply {
                    put("role", "system")
                    put("content", etTestCondition.text.toString())
                })
                .put(JSONObject().apply {
                    put("role", "user")
                    put("content", etTestInput.text.toString())
                })

            // 构建请求体
            val requestBody = JSONObject().apply {
                put("model", "gpt-4o-mini")
                put("messages", messages)
            }.toString().toRequestBody("application/json".toMediaType())

            // 创建一个 GET 请求
            val request = Request.Builder()
                .url("$BASE_URL/chat/completions")
                .header("Authorization", "Bearer $API_KEY")
                .post(requestBody)
                .build()

            // 使用 client 发送请求并获取响应
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("bixiaofu", "onFailure, e: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        // 读取响应体
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            val jsonResponse = JSONObject(it)
                            val content = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                            Log.e("bixiaofu", "Response: $content")
                            runOnUiThread {
                                tvTestResult.text = content
                            }
                        }
                    } else {
                        Log.e("bixiaofu", "Request failed: ${response.code}")
                    }
                }
            })
        }
    }
}