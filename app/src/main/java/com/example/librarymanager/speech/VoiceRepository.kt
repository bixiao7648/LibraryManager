package com.example.librarymanager.speech

import android.content.Context
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.shareIn
import org.json.JSONObject

interface VoiceRepository {
    val result: Flow<String>
    val speech: Flow<String>
    val ttsPlaying: Flow<Boolean>
    val history: Flow<List<String>>
}

class TestVoiceRepoImpl(context: Context) : VoiceRepository {
    companion object {
        private const val TAG = "IVoiceRepository"
    }

    private val recognizer = IRecognizer.get(context)

    override val result: Flow<String> = recognizer.result
        .onEach {
            Log.d("--------f", "json result: $it")
        }
        .mapLatest {
            try {
                val ob = JSONObject(it).getJSONObject("outparams")
                var prompt_text=ob.getString("prompt_text")
                if ("_".equals(prompt_text)){
                    val inAimcNet=ob.optString("inAimcNet")
                    if (inAimcNet.isNullOrBlank()){
                        JSONObject(inAimcNet)?.apply {
                            if (!isNull("slotParams")){
                                JSONObject(optString("slotParams")).apply {
                                    if (!isNull("features")){
                                        prompt_text=optString("features")
                                    }
                                }
                            }
                        }
                    }
                }
                prompt_text
            } catch (e: Exception) {
                ""
            }
        }.onEach {
            Log.d(TAG, "result: $it")
        }

    override val speech: Flow<String> = recognizer.userSpeech.onEach {
        Log.d(TAG, "user speech: $it")
    }

    override val history = speech.runningFold(mutableListOf<String>()) { list, str ->
        if (str.endsWith(";")) {
            list.add(str.replace(";", ""))
        }
        list
    }


    override val ttsPlaying: Flow<Boolean> =
        recognizer.speaking.shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
}