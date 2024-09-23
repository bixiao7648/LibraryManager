package com.example.librarymanager.speech

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.iflytek.aiui.AIUIAgent
import com.iflytek.aiui.AIUIConstant
import com.iflytek.aiui.AIUIEvent
import com.iflytek.aiui.AIUIMessage
import com.iflytek.aiui.AIUISetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.transformLatest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


interface IRecognizer {
    val result: Flow<String>
    val userSpeech: Flow<String>
    val speaking: Flow<Boolean>
    suspend fun tts(content: String)

    companion object {
        private var instance: IRecognizer? = null

        @Synchronized
        fun get(context: Context): IRecognizer {
            if (instance == null) {
                instance = AIUIRecognizer(context)
            }
            return instance!!
        }
    }
}

internal class AIUIRecognizer(context: Context) : IRecognizer {
    companion object {
        private const val TAG = "AIUIRecognizer"
    }


    private var state = AIUIConstant.STATE_IDLE
    private val config = context.assets.open("cfg/aiui_basis.cfg").bufferedReader().readText()
    private var eventCallback: (AIUIEvent) -> Unit = {}

    private val agent = AIUIAgent.createAgent(context, config) { event ->
        Log.d("-------------Recognizer", "event: code = ${event.eventType},  info = ${event.info}")
        if (event.eventType == AIUIConstant.EVENT_STATE) {
            state = event.arg1
        }
        eventCallback(event)
    }

    init {
        AIUISetting.setSystemInfo(AIUIConstant.KEY_SERIAL_NUM, "hhhh")
    }

    private fun sendMessage(message: AIUIMessage) {
        //确保AIUI处于唤醒状态
        if (state != AIUIConstant.STATE_WORKING) {
            agent.sendMessage(AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null))
        }
        agent.sendMessage(message)
    }

    private val event: Flow<AIUIEvent> = callbackFlow {
        eventCallback = {
            trySend(it)
        }

        sendMessage(
            AIUIMessage(
                AIUIConstant.CMD_START_RECORD,
                0,
                0,
                "data_type=audio,sample_rate=16000",
                null
            )
        )

        awaitClose {
            sendMessage(
                AIUIMessage(
                    AIUIConstant.CMD_STOP_RECORD,
                    0,
                    0,
                    "data_type=audio,sample_rate=16000",
                    null
                )
            )
        }
    }.onCompletion {
        Log.d(TAG, "recognize finish: ")
    }.flowOn(Dispatchers.IO)
        .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    private val recognizeResult: Flow<Pair<String, String>> =
        event.filter { it.eventType == AIUIConstant.EVENT_RESULT }.transformLatest { event ->
            val data = JSONObject(event.info).getJSONArray("data").getJSONObject(0)
            val sub = data.getJSONObject("params").optString("sub")
            val content = data.getJSONArray("content").getJSONObject(0)
            if (content.has("cnt_id")) {
                val cntId = content.getString("cnt_id")
                val cntData = event.data.getByteArray(cntId)
                if (cntData != null) {
                    val cntString = String(cntData, charset("utf-8"))
                    when (sub) {
                        "iat" -> {
                            val user =
                                JSONArray(cntString).getJSONObject(0).optString("data")
                                    .let {
                                        analysisResultAsr(it)
                                    }
                            if (user.isNotBlank()) {
                                emit(Pair("user", user))
                            }
                        }

                        "dics" -> {
                            val cs = JSONArray(cntString)
                            val cntJson = cs.getJSONObject(0)
                            if ("dics" == cntJson.getString("sub")) {
                                val data = cntJson.getString("data")
                                emit(Pair("result", data))
                            }
                        }
                    }
                }
            }
        }.shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    override val result: Flow<String> =
        recognizeResult.filter { it.first == "result" }.map { it.second }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    override val userSpeech: Flow<String> =
        recognizeResult.filter { it.first == "user" }.map { it.second }.onEach {
            Log.d(TAG, "user: $it")
        }.shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    override val speaking: Flow<Boolean> =
        event.filter { it.eventType == AIUIConstant.EVENT_TTS }.mapLatest {
            val ttsEvent = it.arg1
            when (ttsEvent) {
                AIUIConstant.TTS_SPEAK_BEGIN, AIUIConstant.TTS_SPEAK_RESUMED, AIUIConstant.TTS_SPEAK_PROGRESS -> true
                else -> false
            }
        }.shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    override suspend fun tts(content: String) {
        sendMessage(
            AIUIMessage(
                AIUIConstant.CMD_TTS,
                AIUIConstant.START, 0, null, content.toByteArray()
            )
        )
    }

    private val mAsrPgsStack = Array(256) { "" }

    @Throws(JSONException::class)
    private fun analysisResultAsr(string: String): String {
        if (TextUtils.isEmpty(string)) {
            return ""
        }
        val text = JSONObject(string)
        // 解析拼接此次听写结果
        val asrVoice = StringBuilder()
        val words = text.optJSONArray("ws")
        val lastResult = text.optBoolean("ls")
        for (i in 0 until words.length()) {
            val word = words.optJSONObject(i).optJSONArray("cw")
            for (j in 0 until word.length()) {
                asrVoice.append(word.optJSONObject(j).opt("w"))
            }
        }
        val asrResult = StringBuffer()
        val pgsMode = text.optString("pgs")
        // 非PGS模式结果
        if (TextUtils.isEmpty(pgsMode)) {
            if (TextUtils.isEmpty(asrVoice)) {
                return ""
            }
            asrResult.append(asrVoice)
        } else {
            mAsrPgsStack[text.optInt("sn")] = asrVoice.toString()
            // pgs结果两种模式rpl和apd模式（替换和追加模式）
            if (pgsMode == "rpl") {
                // 根据replace指定的range，清空stack中对应位置值
                val range = text.optJSONArray("rg")
                val start = range.optInt(0)
                val end = range.optInt(1)
                for (index in start..end) {
                    mAsrPgsStack[index] = ""
                }
            }
            val pgsResult = StringBuilder()
            // 汇总stack经过操作后的剩余的有效结果信息
            for (index in mAsrPgsStack.indices) {
                if (TextUtils.isEmpty(mAsrPgsStack.get(index))) {
                    continue
                }
                pgsResult.append(mAsrPgsStack.get(index))
                // 如果是最后一条听写结果，则清空stack便于下次使用
                if (lastResult) {
                    mAsrPgsStack[index] = ""
                }
            }
            asrResult.append(pgsResult)
        }
        if (!TextUtils.isEmpty(asrResult)) {
            if (!lastResult) {
                return asrResult.toString()
            } else {
                return asrResult.append(";").toString()
            }
        }

        return ""
    }
}