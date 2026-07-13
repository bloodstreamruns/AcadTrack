package com.example.acadtrack_beta

import android.app.Application
import com.example.acadtrack_beta.data.repository.SesionRepository
import com.example.acadtrack_beta.data.repository.TareaRepository

class AcadTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TareaRepository.init(this)
        SesionRepository.init(this)
    }
}