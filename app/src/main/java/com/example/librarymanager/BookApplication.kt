package com.example.librarymanager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class BookApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun getContext(): Context = context
    }
}