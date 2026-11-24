package com.example.proyectofinaltandaclara.ui.screens.detalle

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinaltandaclara.data.model.Periodicidad
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pantalla principal del Detalle de Tanda con 3 pestañas: Pagos, Turnos y Resumen.
 * Ofrece acceso rápido a la gestión de participantes, edición y eliminación de la tanda.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTandaScreen(
    viewModel: DetalleTandaViewModel,
    onVolver: () -> Unit,
    onGestionarParticipantes: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var tabIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.tanda?.nombre ?: "Detalle de Tanda",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    uiState.tanda?.let { tanda ->
                        IconButton(onClick = { onGestionarParticipantes(tanda.id) }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Participantes",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = { viewModel.abrirDialogoEditar() }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Tanda",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = { viewModel.abrirDialogoEliminar() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar Tanda",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.tanda == null -> {
                    Text(
                        text = "La tanda no fue encontrada.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TabRow(
                            selectedTabIndex = tabIndex,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            Tab(
                                selected = tabIndex == 0,
                                onClick = { tabIndex = 0 },
                                text = { Text("Pagos", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = tabIndex == 1,
                                onClick = { tabIndex = 1 },
                                text = { Text("Turnos", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = tabIndex == 2,
                                onClick = { tabIndex = 2 },
                                text = { Text("Resumen", fontWeight = FontWeight.Bold) }
                            )
                        }

                        when (tabIndex) {
                            0 -> PestanaPagos(
                                uiState = uiState,
                                onSeleccionarPeriodo = { viewModel.seleccionarPeriodo(it) },
                                onTogglePago = { pId, periodo -> viewModel.togglePago(pId, periodo) }
                            )
                            1 -> PestanaTurnos(uiState = uiState)
                            2 -> PestanaResumen(uiState = uiState)
                        }
                    }
                }
            }

            // Diálogo de Edición de Tanda
            if (uiState.mostrarDialogoEditar) {
                AlertDialog(
                    onDismissRequest = { viewModel.cerrarDialogoEditar() },
                    title = { Text("Editar Tanda", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.editNombre,
                                onValueChange = { viewModel.onEditNombreChanged(it) },
                                label = { Text("Nombre de la tanda") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = uiState.editMontoText,
                                onValueChange = { viewModel.onEditMontoChanged(it) },
                                label = { Text("Monto por persona ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            Text("Periodicidad:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Periodicidad.entries.forEach { p ->
                                    FilterChip(
                                        selected = uiState.editPeriodicidad == p,
                                        onClick = { viewModel.onEditPeriodicidadChanged(p) },
                                        label = {
                                            Text(
                                                text = when (p) {
                                                    Periodicidad.SEMANAL -> "Semanal"
                                                    Periodicidad.QUINCENAL -> "Quincenal"
                                                    Periodicidad.MENSUAL -> "Mensual"
                                                },
                                                fontSize = 11.sp
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "MX"))
                            OutlinedTextField(
                                value = uiState.editFechaInicio.format(formatter),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Fecha de inicio") },
                                leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null) },
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val f = uiState.editFechaInicio
                                        DatePickerDialog(
                                            context,
                                            { _, y, m, d ->
                                                viewModel.onEditFechaInicioChanged(java.time.LocalDate.of(y, m + 1, d))
                                            },
                                            f.year, f.monthValue - 1, f.dayOfMonth
                                        ).show()
                                    },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = { viewModel.guardarEdicionTanda() }) {
                            Text("Guardar Cambios")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.cerrarDialogoEditar() }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Diálogo de Confirmación para Eliminar Tanda
            if (uiState.mostrarDialogoEliminar) {
                AlertDialog(
                    onDismissRequest = { viewModel.cerrarDialogoEliminar() },
                    title = { Text("Eliminar Tanda", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
                    text = {
                        Text("¿Estás seguro de que deseas eliminar la tanda '${uiState.tanda?.nombre}'? Esta acción eliminará permanentemente todos sus integrantes y registros de pago.")
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.eliminarTanda(onEliminada = onVolver) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.cerrarDialogoEliminar() }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
