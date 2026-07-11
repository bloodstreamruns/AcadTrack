package com.example.acadtrack_beta.ui.screens.asignaturas

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.acadtrack_beta.data.model.Asignatura

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignaturasScreen(
    onNuevaAsignatura: () -> Unit,
    onEditarAsignatura: (String) -> Unit,
    viewModel: AsignaturaViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Asignaturas") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaAsignatura) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva asignatura")
            }
        }
    ) { paddingValues ->
        if (uiState.asignaturas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes asignaturas.\nToca \"+\" para agregar una.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.asignaturas, key = { it.id }) { asignatura ->
                    AsignaturaCard(
                        asignatura = asignatura,
                        onEditar = { onEditarAsignatura(asignatura.id) },
                        onEliminar = { viewModel.onEliminarClicked(asignatura) }
                    )
                }
            }
        }
    }

    uiState.asignaturaAEliminar?.let { asignatura ->
        AlertDialog(
            onDismissRequest = viewModel::onCancelarEliminar,
            title = { Text("Eliminar asignatura") },
            text = { Text("¿Seguro que deseas eliminar \"${asignatura.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmarEliminar) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onCancelarEliminar) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun AsignaturaCard(
    asignatura: Asignatura,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asignatura.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = asignatura.codigo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (asignatura.profesor.isNotBlank()) {
                    Text(
                        text = asignatura.profesor,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = asignatura.semestre,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEditar) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        }
    }
}