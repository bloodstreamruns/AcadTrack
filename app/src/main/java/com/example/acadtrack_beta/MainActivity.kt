package com.example.acadtrack_beta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.acadtrack_beta.ui.screens.asignaturas.AsignaturaForm
import com.example.acadtrack_beta.ui.screens.asignaturas.AsignaturaViewModel
import com.example.acadtrack_beta.ui.screens.asignaturas.AsignaturasScreen
import com.example.acadtrack_beta.ui.screens.login.LoginScreen

private object Rutas {
    const val LOGIN = "login"
    const val ASIGNATURAS = "asignaturas"
    const val ASIGNATURA_FORM = "asignaturaForm?asignaturaId={asignaturaId}"

    fun asignaturaFormCrear() = "asignaturaForm"
    fun asignaturaFormEditar(id: String) = "asignaturaForm?asignaturaId=$id"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    val navController = rememberNavController()
                    val asignaturaViewModel: AsignaturaViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = Rutas.LOGIN
                    ) {
                        composable(Rutas.LOGIN) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(Rutas.ASIGNATURAS) {
                                        popUpTo(Rutas.LOGIN) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {

                                }
                            )
                        }

                        composable(Rutas.ASIGNATURAS) {
                            AsignaturasScreen(
                                onNuevaAsignatura = {
                                    navController.navigate(Rutas.asignaturaFormCrear())
                                },
                                onEditarAsignatura = { id ->
                                    navController.navigate(Rutas.asignaturaFormEditar(id))
                                },
                                viewModel = asignaturaViewModel
                            )
                        }

                        composable(
                            route = Rutas.ASIGNATURA_FORM,
                            arguments = listOf(
                                navArgument("asignaturaId") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val asignaturaId = backStackEntry.arguments?.getString("asignaturaId")
                            AsignaturaForm(
                                asignaturaId = asignaturaId,
                                onGuardadoExitoso = {
                                    navController.popBackStack()
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                viewModel = asignaturaViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}