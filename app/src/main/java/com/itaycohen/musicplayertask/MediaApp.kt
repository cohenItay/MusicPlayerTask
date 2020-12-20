package com.itaycohen.musicplayertask

import android.app.Application
import com.itaycohen.musicplayertask.logics.AudioItemsInitLogic

class MediaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AudioItemsInitLogic().run(applicationContext)

    }
}