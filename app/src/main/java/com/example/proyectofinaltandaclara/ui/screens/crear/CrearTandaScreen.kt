package com.example.proyectofinaltandaclara.ui.screens.crear

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
 * Formulario para registrar una nueva tanda con nombre, monto individual,
 * periodicidad y fecha de inicio seleccionable mediante DatePicker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearTandaScreen(
    viewModel: CrearTandaViewModel,
    onVolver: () -> Unit,
    onTandaCreada: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.tandaCreadaId) {
        uiState.tandaCreadaId?.let { id ->
            viewModel.resetNavegacion()
            onTandaCreada(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nueva Tanda",
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Datos de la Tanda",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Nombre de la tanda
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = { viewModel.onNombreChanged(it) },
                label = { Text("Nombre de la tanda") },
                placeholder = { Text("Ej. Tanda Navideña / Oficina") },
                isError = uiState.errorNombre != null,
                supportingText = uiState.errorNombre?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Monto por persona
            OutlinedTextField(
                value = uiState.montoText,
                onValueChange = { viewModel.onMontoChanged(it) },
                label = { Text("Monto por persona ($)") },
                placeholder = { Text("Ej. 500") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.errorMonto != null,
                supportingText = uiState.errorMonto?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Selector de Periodicidad
            Text(
                text = "Periodicidad del cobro/pago",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Periodicidad.entries.forEach { item ->
                    FilterChip(
                        selected = uiState.periodicidad == item,
                        onClick = { viewModel.onPeriodicidadSelected(item) },
                        label = {
                            Text(
                                text = when (item) {
                                    Periodicidad.SEMANAL -> "Semanal"
                                    Periodicidad.QUINCENAL -> "Quincenal"
                                    Periodicidad.MENSUAL -> "Mensual"
                                },
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Fecha de inicio (DatePicker Dialog)
            val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))
            val fechaTexto = uiState.fechaInicio.format(formatter)

            OutlinedTextField(
                value = fechaTexto,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de inicio") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val actual = uiState.fechaInicio
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                viewModel.onFechaInicioChanged(
                                    java.time.LocalDate.of(year, month + 1, dayOfMonth)
                                )
                            },
                            actual.year,
                            actual.monthValue - 1,
                            actual.dayOfMonth
                        ).show()
                    },
                enabled = false,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Guardar
            Button(
                onClick = { viewModel.guardarTanda() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.guardando,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.guardando) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.height(24.dp)
                    )
                } else {
                    Text(
                        text = "Guardar y agregar participantes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
