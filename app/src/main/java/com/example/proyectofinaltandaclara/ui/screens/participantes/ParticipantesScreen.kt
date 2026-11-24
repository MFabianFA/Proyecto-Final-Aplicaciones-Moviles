package com.example.proyectofinaltandaclara.ui.screens.participantes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinaltandaclara.data.model.Participante
import com.example.proyectofinaltandaclara.ui.theme.DoradoAcento
import com.example.proyectofinaltandaclara.ui.theme.DoradoSuave
import com.example.proyectofinaltandaclara.ui.theme.VerdeOscuro

/**
 * Pantalla de gestión de integrantes para una tanda específica.
 * Permite agregar, editar, eliminar, sortear turnos aleatoriamente y
 * reordenar turnos de forma manual (mover arriba/abajo).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantesScreen(
    viewModel: ParticipantesViewModel,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.tanda?.nombre ?: "Participantes",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.abrirDialogoNuevo() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar participante")
            }
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

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Fila superior: Contador y Botón Sortear Turnos
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Integrantes (${uiState.participantes.size})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdeOscuro
                            )

                            OutlinedButton(
                                onClick = { viewModel.sortearTurnos() },
                                enabled = uiState.participantes.size > 1,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sortear turnos", fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (uiState.participantes.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Aún no hay integrantes registrados",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Presiona el botón '+' para agregar el primer participante.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                itemsIndexed(
                                    items = uiState.participantes,
                                    key = { _, item -> item.id }
                                ) { index, participante ->
                                    ParticipanteItemCard(
                                        participante = participante,
                                        esPrimero = index == 0,
                                        esUltimo = index == uiState.participantes.size - 1,
                                        onMoverArriba = { viewModel.moverArriba(index) },
                                        onMoverAbajo = { viewModel.moverAbajo(index) },
                                        onEditar = { viewModel.abrirDialogoEditar(participante) },
                                        onEliminar = { viewModel.eliminarParticipante(participante) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Diálogo emergente para agregar / editar participante
            if (uiState.mostrarDialogoAgregarEditar) {
                AlertDialog(
                    onDismissRequest = { viewModel.cerrarDialogo() },
                    title = {
                        Text(
                            text = if (uiState.participanteEnEdicion == null) "Nuevo Participante" else "Editar Participante",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.nombreInput,
                                onValueChange = { viewModel.onNombreChanged(it) },
                                label = { Text("Nombre completo *") },
                                isError = uiState.errorNombre != null,
                                supportingText = uiState.errorNombre?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = uiState.telefonoInput,
                                onValueChange = { viewModel.onTelefonoChanged(it) },
                                label = { Text("Teléfono (Opcional)") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = { viewModel.guardarParticipante() }) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.cerrarDialogo() }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ParticipanteItemCard(
    participante: Participante,
    esPrimero: Boolean,
    esUltimo: Boolean,
    onMoverArriba: () -> Unit,
    onMoverAbajo: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo con el número de turno
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DoradoSuave),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${participante.turno}",
                    fontWeight = FontWeight.ExtraBold,
                    color = DoradoAcento,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nombre y Teléfono
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = participante.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!participante.telefono.isNullOrEmpty()) {
                    Text(
                        text = participante.telefono,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botones para mover turno arriba/abajo
            IconButton(
                onClick = onMoverArriba,
                enabled = !esPrimero,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Mover turno arriba"
                )
            }

            IconButton(
                onClick = onMoverAbajo,
                enabled = !esUltimo,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Mover turno abajo"
                )
            }

            // Editar y Eliminar
            IconButton(
                onClick = onEditar,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar participante",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onEliminar,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar participante",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
