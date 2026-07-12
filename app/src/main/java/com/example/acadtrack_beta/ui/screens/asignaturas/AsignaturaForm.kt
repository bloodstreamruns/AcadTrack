package com.example.acadtrack_beta.ui.screens.asignaturas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AsignaturaForm(
    onGuardado: () -> Unit,
    onCancelar: () -> Unit,
    viewModel: AsignaturaViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()

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
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Nueva asignatura",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = formState.nombre,
                onValueChange = viewModel::onNombreChanged,
                label = { Text("Nombre") },
                isError = formState.nombreError != null,
                supportingText = { formState.nombreError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.codigo,
                onValueChange = viewModel::onCodigoChanged,
                label = { Text("Código") },
                isError = formState.codigoError != null,
                supportingText = { formState.codigoError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formState.profesor,
                onValueChange = viewModel::onProfesorChanged,
                label = { Text("Profesor (opcional)") },
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

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.width(12.dp))
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
}