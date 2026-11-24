package com.example.proyectofinaltandaclara.ui.screens.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinaltandaclara.ui.components.TandaCard
import com.example.proyectofinaltandaclara.ui.theme.VerdeOscuro

/**
 * Pantalla principal "Mis tandas": Lista de tandas activas/terminadas,
 * botón flotante '+' para crear una nueva, opción para cargar la tanda demostrativa
 * y mensaje cuando el listado está vacío.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTandasScreen(
    viewModel: ListaTandasViewModel,
    onCrearTandaClick: () -> Unit,
    onTandaClick: (Long) -> Unit,
    onGestionarParticipantesClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Tandas",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.cargarTandaEjemplo() }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Cargar tanda de ejemplo",
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
                onClick = onCrearTandaClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva Tanda"
                )
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.tandas.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = VerdeOscuro
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "¡Aún no tienes ninguna tanda organizada!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = VerdeOscuro
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Comienza presionando el botón '+' o carga una tanda demostrativa para la evaluación.",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedButton(
                            onClick = { viewModel.cargarTandaEjemplo() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Cargar tanda de ejemplo", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = uiState.tandas,
                            key = { it.tanda.id }
                        ) { resumen ->
                            TandaCard(
                                resumen = resumen,
                                onClick = { onTandaClick(resumen.tanda.id) },
                                onGestionarParticipantes = { onGestionarParticipantesClick(resumen.tanda.id) },
                                onEliminar = { viewModel.eliminarTanda(resumen.tanda) }
                            )
                        }
                    }
                }
            }
        }
    }
}
