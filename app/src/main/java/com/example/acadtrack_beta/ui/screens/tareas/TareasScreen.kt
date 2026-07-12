package com.example.acadtrack_beta.ui.screens.tareas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acadtrack_beta.data.model.Tarea

@Composable
fun TareasScreen(
    onAgregarClick: () -> Unit,
    onEditarClick: (Tarea) -> Unit,
    viewModel: TareaViewModel = viewModel()
) {
    val tareas by viewModel.tareas.collectAsStateWithLifecycle()
    val asignaturas by viewModel.asignaturas.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarClick) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { paddingValues ->
        if (tareas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes tareas. Toca + para agregar una.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(tareas, key = { it.id }) { tarea ->
                    val nombreAsignatura = asignaturas.find { it.id == tarea.asignaturaId }?.nombre
                        ?: "Sin asignatura"
                    TareaCard(
                        tarea = tarea,
                        nombreAsignatura = nombreAsignatura,
                        onEditar = { onEditarClick(tarea) },
                        onEliminar = { viewModel.eliminar(tarea.id) },
                        onToggleCompletada = { viewModel.marcarCompletada(tarea, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TareaCard(
    tarea: Tarea,
    nombreAsignatura: String,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onToggleCompletada: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = tarea.completada, onCheckedChange = onToggleCompletada)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                )
                Text(
                    text = "$nombreAsignatura · ${tarea.fechaEntrega.toLocalDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${tarea.tipo.name} · ${tarea.prioridad.name}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}