package com.example.acadtrack_beta.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private val Context.dataStoreSesion by preferencesDataStore(name = "sesion_prefs")

object SesionRepository {

    private val EMAIL_KEY = stringPreferencesKey("email_usuario")
    private val repositorioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _emailUsuario = MutableStateFlow<String?>(null)
    val emailUsuario: StateFlow<String?> = _emailUsuario

    private val _sesionCargada = MutableStateFlow(false)
    val sesionCargada: StateFlow<Boolean> = _sesionCargada

    private lateinit var dataStore: DataStore<Preferences>

    fun init(context: Context) {
        if (::dataStore.isInitialized) return
        dataStore = context.applicationContext.dataStoreSesion

        dataStore.data
            .map { it[EMAIL_KEY] }
            .onEach { email ->
                _emailUsuario.value = email
                _sesionCargada.value = true
            }
            .launchIn(repositorioScope)
    }

    fun iniciarSesion(email: String) {
        _emailUsuario.value = email
        repositorioScope.launch {
            dataStore.edit { prefs -> prefs[EMAIL_KEY] = email }
        }
    }

    fun cerrarSesion() {
        _emailUsuario.value = null
        repositorioScope.launch {
            dataStore.edit { prefs -> prefs.remove(EMAIL_KEY) }
        }
    }
}