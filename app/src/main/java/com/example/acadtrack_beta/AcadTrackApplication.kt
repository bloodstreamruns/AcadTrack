package com.example.acadtrack_beta

import android.app.Application
import com.example.acadtrack_beta.data.repository.SesionRepository

class AcadTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SesionRepository.init(this)   // DataStore sigue necesitando Context
    }
}