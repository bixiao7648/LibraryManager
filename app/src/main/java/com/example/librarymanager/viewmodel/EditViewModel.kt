package com.example.librarymanager.viewmodel

import androidx.lifecycle.ViewModel

class EditViewModel : ViewModel() {
    // When we rotate the screen, the activity will be recreated. And we will lose the current data.
    // So move the values here.
    var title = ""
    var author = ""
    var publishYear = ""
    var isbn = ""
}