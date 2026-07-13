package com.example.acadtrack_beta.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var tareaSeleccionada by remember { mutableStateOf<TareaConAsignatura?>(null) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Inicio", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenCard(
                    titulo = "Asignaturas",
                    valor = uiState.totalAsignaturas.toString(),
                    modifier = Modifier.weight(1f)
                )
                ResumenCard(
                    titulo = "Pendientes",
                    valor = uiState.totalPendientes.toString(),
                    modifier = Modifier.weight(1f)
                )
                ResumenCard(
                    titulo = "Atrasadas",
                    valor = uiState.totalAtrasadas.toString(),
                    modifier = Modifier.weight(1f),
                    esAlerta = uiState.totalAtrasadas > 0
                )
            }

            Spacer(Modifier.height(24.dp))
            Text("Tareas pendientes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (uiState.tareasPendientes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes tareas pendientes.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.tareasPendientes, key = { it.tarea.id }) { item ->
                        TareaPendienteCard(
                            item = item,
                            onClick = { tareaSeleccionada = item },
                            onToggleCompletada = { viewModel.marcarCompletada(item.tarea, it) }
                        )
                    }
                }
            }
        }
    }

    tareaSeleccionada?.let { item ->
        DetalleTareaDialog(
            item = item,
            onDismiss = { tareaSeleccionada = null }
        )
    }
}

@Composable
private fun ResumenCard(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    esAlerta: Boolean = false
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                color = if (esAlerta) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TareaPendienteCard(
    item: TareaConAsignatura,
    onClick: () -> Unit,
    onToggleCompletada: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.tarea.completada,
                onCheckedChange = onToggleCompletada
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(item.tarea.titulo, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${item.nombreAsignatura} · ${item.tarea.fechaEntrega.toLocalDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (item.atrasada)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (item.atrasada) {
                AssistChip(onClick = {}, label = { Text("Atrasada") })
            }
        }
    }
}

// Ventana de solo lectura: mostrar detalles, no editar.
// Editar solo está disponible desde la pantalla de Tareas.
@Composable
private fun DetalleTareaDialog(
    item: TareaConAsignatura,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        },
        title = { Text(item.tarea.titulo) },
        text = {
            Column {
                DetalleFila("Asignatura", item.nombreAsignatura)
                DetalleFila("Fecha de entrega", item.tarea.fechaEntrega.toLocalDate().toString())
                DetalleFila("Tipo", item.tarea.tipo.name)
                DetalleFila("Prioridad", item.tarea.prioridad.name)
                DetalleFila(
                    "Estado",
                    when {
                        item.tarea.completada -> "Completada"
                        item.atrasada -> "Atrasada"
                        else -> "Pendiente"
                    }
                )

                if (item.tarea.descripcion.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Descripción",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(item.tarea.descripcion, style = MaterialTheme.typography.bodyMedium)
                }

                if (item.tarea.notas.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Notas",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(item.tarea.notas, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    )
}

@Composable
private fun DetalleFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(valor, style = MaterialTheme.typography.bodySmall)
    }
}