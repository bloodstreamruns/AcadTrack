package com.example.acadtrack_beta.ui.screens.tareas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.acadtrack_beta.data.model.Prioridad
import com.example.acadtrack_beta.data.model.TipoTarea
import java.time.Instant
import java.time.ZoneOffset

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaForm(
    onGuardado: () -> Unit,
    onCancelar: () -> Unit,
    viewModel: TareaViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val asignaturas by viewModel.asignaturas.collectAsStateWithLifecycle()

    var expandedAsignatura by remember { mutableStateOf(false) }
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedPrioridad by remember { mutableStateOf(false) }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(formState.guardadoExitoso) {
        if (formState.guardadoExitoso) {
            viewModel.consumeGuardadoExitoso()
            viewModel.limpiarFormulario()
            onGuardado()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // formulario largo: Tema 4 - ScrollView
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text("Nueva tarea", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(24.dp))

            if (asignaturas.isEmpty()) {
                Text(
                    text = "Primero crea al menos una asignatura.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = formState.titulo,
                onValueChange = viewModel::onTituloChanged,
                label = { Text("Título") },
                isError = formState.tituloError != null,
                supportingText = { formState.tituloError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Selector de asignatura
            ExposedDropdownMenuBox(
                expanded = expandedAsignatura,
                onExpandedChange = { expandedAsignatura = it }
            ) {
                OutlinedTextField(
                    value = formState.asignaturaNombre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Asignatura") },
                    isError = formState.asignaturaError != null,
                    supportingText = { formState.asignaturaError?.let { Text(it) } },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAsignatura)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedAsignatura,
                    onDismissRequest = { expandedAsignatura = false }
                ) {
                    asignaturas.forEach { asignatura ->
                        DropdownMenuItem(
                            text = { Text("${asignatura.nombre} (${asignatura.codigo})") },
                            onClick = {
                                viewModel.onAsignaturaSeleccionada(asignatura)
                                expandedAsignatura = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Selector de fecha (DatePicker Material 3)
            OutlinedTextField(
                value = formState.fechaEntrega?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de entrega") },
                isError = formState.fechaError != null,
                supportingText = { formState.fechaError?.let { Text(it) } },
                trailingIcon = {
                    IconButton(onClick = { mostrarDatePicker = true }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Elegir fecha")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDatePicker = true }
            )
            Spacer(Modifier.height(12.dp))

            // Tipo de tarea
            ExposedDropdownMenuBox(
                expanded = expandedTipo,
                onExpandedChange = { expandedTipo = it }
            ) {
                OutlinedTextField(
                    value = formState.tipo.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedTipo,
                    onDismissRequest = { expandedTipo = false }
                ) {
                    TipoTarea.entries.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo.name) },
                            onClick = {
                                viewModel.onTipoSeleccionado(tipo)
                                expandedTipo = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Prioridad
            ExposedDropdownMenuBox(
                expanded = expandedPrioridad,
                onExpandedChange = { expandedPrioridad = it }
            ) {
                OutlinedTextField(
                    value = formState.prioridad.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Prioridad") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrioridad)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPrioridad,
                    onDismissRequest = { expandedPrioridad = false }
                ) {
                    Prioridad.entries.forEach { prioridad ->
                        DropdownMenuItem(
                            text = { Text(prioridad.name) },
                            onClick = {
                                viewModel.onPrioridadSeleccionada(prioridad)
                                expandedPrioridad = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.notas,
                onValueChange = viewModel::onNotasChanged,
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f)) {
                    Text("Cancelar")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = viewModel::guardar,
                    enabled = formState.isFormValid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }
    }

    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.fechaEntrega
                ?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val fecha = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        viewModel.onFechaSeleccionada(fecha)
                    }
                    mostrarDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}