package com.example.acadtrack_beta.ui.screens.asignaturas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignaturaForm(
    asignaturaId: String? = null,
    onGuardadoExitoso: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AsignaturaViewModel
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(asignaturaId) {
        if (asignaturaId != null) {
            viewModel.onEditarAsignatura(asignaturaId)
        } else {
            viewModel.onNuevaAsignatura()
        }
    }

    LaunchedEffect(formState.isGuardadoExitoso) {
        if (formState.isGuardadoExitoso) {
            onGuardadoExitoso()
            viewModel.consumeGuardadoExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (formState.isEditando) "Editar asignatura" else "Nueva asignatura") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            OutlinedTextField(
                value = formState.nombre,
                onValueChange = viewModel::onNombreChanged,
                label = { Text("Nombre") },
                singleLine = true,
                isError = formState.nombreError != null,
                supportingText = {
                    formState.nombreError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.codigo,
                onValueChange = viewModel::onCodigoChanged,
                label = { Text("Código") },
                singleLine = true,
                isError = formState.codigoError != null,
                supportingText = {
                    formState.codigoError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.profesor,
                onValueChange = viewModel::onProfesorChanged,
                label = { Text("Profesor") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.semestre,
                onValueChange = viewModel::onSemestreChanged,
                label = { Text("Semestre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::onGuardarClicked,
                enabled = formState.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}