package com.example.librarymanager.speech

import androidx.lifecycle.ViewModel
import com.example.librarymanager.BookApplication
import kotlinx.coroutines.flow.Flow

class SpeechViewModel : ViewModel() {

    private val voiceRepo = TestVoiceRepoImpl(BookApplication.getContext())

    val speaking = voiceRepo.ttsPlaying

    val speechContent = voiceRepo.speech

    val speakHistory: Flow<List<String>> = voiceRepo.history

    val result = voiceRepo.result
}