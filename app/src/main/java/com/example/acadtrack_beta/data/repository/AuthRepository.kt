package com.example.acadtrack_beta.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

// Reemplaza a SesionRepository: FirebaseAuth ya guarda la sesión por su cuenta,
// no hace falta DataStore para esto.
object AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val repositorioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val usuarioActual: StateFlow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.stateIn(repositorioScope, SharingStarted.Eagerly, auth.currentUser)

    val uid: String? get() = auth.currentUser?.uid

    suspend fun registrar(correo: String, password: String): Result<Unit> = try {
        auth.createUserWithEmailAndPassword(correo, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun iniciarSesion(correo: String, password: String): Result<Unit> = try {
        auth.signInWithEmailAndPassword(correo, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun cerrarSesion() = auth.signOut()
}

