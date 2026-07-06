package com.example.acadtrack_beta

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.acadtrack_beta.ui.screens.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    LoginScreen(
                        onLoginSuccess = {
                            Toast.makeText(this@MainActivity, "Login exitoso", Toast.LENGTH_SHORT).show()
                            // TODO: navegar a la pantalla principal (home)
                        },
                        onNavigateToRegister = {
                            // TODO: navegar a la pantalla de registro
                        }
                    )
                }
            }
        }
    }
}