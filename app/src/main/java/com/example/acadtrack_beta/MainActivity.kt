package com.example.acadtrack_beta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home

import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.acadtrack_beta.data.repository.SesionRepository
import com.example.acadtrack_beta.ui.screens.asignaturas.AsignaturaForm
import com.example.acadtrack_beta.ui.screens.asignaturas.AsignaturaViewModel
import com.example.acadtrack_beta.ui.screens.asignaturas.AsignaturasScreen
import com.example.acadtrack_beta.ui.screens.home.HomeScreen
import com.example.acadtrack_beta.ui.screens.login.LoginScreen
import com.example.acadtrack_beta.ui.screens.perfil.PerfilScreen
import com.example.acadtrack_beta.ui.screens.tareas.TareaForm
import com.example.acadtrack_beta.ui.screens.tareas.TareaViewModel
import com.example.acadtrack_beta.ui.screens.tareas.TareasScreen

// Rutas de todo el grafo de navegación.
// Los formularios no reciben id por la ruta: el ViewModel compartido ya trae
// cargarParaEditar()/limpiarFormulario(), así que MainActivity solo decide
// CUÁNDO llamarlos antes de navegar (ver composable(Rutas.ASIGNATURAS) y TAREAS).
private object Rutas {
    const val LOGIN = "login"
    const val HOME = "home"
    const val ASIGNATURAS = "asignaturas"
    const val ASIGNATURA_FORM = "asignaturaForm"
    const val TAREAS = "tareas"
    const val TAREA_FORM = "tareaForm"
    const val PERFIL = "perfil"
}

// Destinos visibles en la barra inferior, una vez el usuario inició sesión.
private data class DestinoBarra(val ruta: String, val label: String, val icono: androidx.compose.ui.graphics.vector.ImageVector)

private val destinosBarra = listOf(
    DestinoBarra(Rutas.HOME, "Inicio", Icons.Filled.Home),
    DestinoBarra(Rutas.ASIGNATURAS, "Asignaturas", Icons.AutoMirrored.Filled.List),
    DestinoBarra(Rutas.TAREAS, "Tareas", Icons.Filled.CheckCircle),
    DestinoBarra(Rutas.PERFIL, "Perfil", Icons.Filled.Person),
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Esperamos a que SesionRepository termine de leer DataStore antes
            // de decidir si arrancamos en Login o directo en Home.
            val sesionLista by SesionRepository.sesionCargada.collectAsStateWithLifecycle()

            if (!sesionLista) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val emailUsuario by SesionRepository.emailUsuario.collectAsStateWithLifecycle()
                val destinoInicial = if (emailUsuario != null) Rutas.HOME else Rutas.LOGIN

                val navController = rememberNavController()

                // ViewModels compartidos a nivel de Activity: la misma instancia
                // se usa en el listado y en el formulario de cada feature, para
                // que cargarParaEditar()/limpiarFormulario() surtan efecto en ambos.
                val asignaturaViewModel: AsignaturaViewModel = viewModel()
                val tareaViewModel: TareaViewModel = viewModel()

                val backStackEntry by navController.currentBackStackEntryAsState()
                val rutaActual = backStackEntry?.destination?.hierarchy?.firstOrNull()?.route
                val mostrarBarraInferior = destinosBarra.any { it.ruta == rutaActual }

                Scaffold(
                    modifier = Modifier,
                    bottomBar = {
                        if (mostrarBarraInferior) {
                            NavigationBar {
                                destinosBarra.forEach { destino ->
                                    NavigationBarItem(
                                        selected = rutaActual == destino.ruta,
                                        onClick = {
                                            navController.navigate(destino.ruta) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(destino.icono, contentDescription = destino.label) },
                                        label = { Text(destino.label) }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = destinoInicial,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Rutas.LOGIN) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(Rutas.HOME) {
                                        popUpTo(Rutas.LOGIN) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {}
                            )
                        }

                        composable(Rutas.HOME) {
                            HomeScreen()
                        }

                        composable(Rutas.ASIGNATURAS) {
                            AsignaturasScreen(
                                onAgregarClick = {
                                    asignaturaViewModel.limpiarFormulario()
                                    navController.navigate(Rutas.ASIGNATURA_FORM)
                                },
                                onEditarClick = { asignatura ->
                                    asignaturaViewModel.cargarParaEditar(asignatura)
                                    navController.navigate(Rutas.ASIGNATURA_FORM)
                                },
                                viewModel = asignaturaViewModel
                            )
                        }

                        composable(Rutas.ASIGNATURA_FORM) {
                            AsignaturaForm(
                                onGuardado = { navController.popBackStack() },
                                onCancelar = { navController.popBackStack() },
                                viewModel = asignaturaViewModel
                            )
                        }

                        composable(Rutas.TAREAS) {
                            TareasScreen(
                                onAgregarClick = {
                                    tareaViewModel.limpiarFormulario()
                                    navController.navigate(Rutas.TAREA_FORM)
                                },
                                onEditarClick = { tarea ->
                                    tareaViewModel.cargarParaEditar(tarea)
                                    navController.navigate(Rutas.TAREA_FORM)
                                },
                                viewModel = tareaViewModel
                            )
                        }

                        composable(Rutas.TAREA_FORM) {
                            TareaForm(
                                onGuardado = { navController.popBackStack() },
                                onCancelar = { navController.popBackStack() },
                                viewModel = tareaViewModel
                            )
                        }

                        composable(Rutas.PERFIL) {
                            PerfilScreen(
                                onCerrarSesion = {
                                    navController.navigate(Rutas.LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}