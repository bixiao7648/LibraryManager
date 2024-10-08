package com.example.librarymanager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.orhanobut.logger.LogLevel
import com.orhanobut.logger.Logger

class BookApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        Logger.init("RecorderLogger").logLevel(LogLevel.FULL).methodCount(8).hideThreadInfo()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun getContext(): Context = context
    }
}