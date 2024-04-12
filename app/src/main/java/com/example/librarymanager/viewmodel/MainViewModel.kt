package com.example.librarymanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var searchViewContent: MutableLiveData<String> = MutableLiveData()

    init {
        searchViewContent.value = ""
    }
}