package com.example.proyectofinaltandaclara.ui.screens.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinaltandaclara.ui.theme.DoradoAcento
import com.example.proyectofinaltandaclara.ui.theme.DoradoSuave
import com.example.proyectofinaltandaclara.ui.theme.VerdeClaroContraste
import com.example.proyectofinaltandaclara.ui.theme.VerdeOscuro
import com.example.proyectofinaltandaclara.ui.theme.VerdePrimario
import com.example.proyectofinaltandaclara.utils.CalculadoraTanda
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pestaña 2: Turnos.
 * Línea de tiempo vertical que lista los periodos, fechas, quién recibe el fondo y estado del turno.
 */
@Composable
fun PestanaTurnos(
    uiState: DetalleTandaUiState
) {
    val tanda = uiState.tanda ?: return
    val totalParticipantes = uiState.participantes.size
    val pActual = uiState.periodoActual

    if (totalParticipantes == 0) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No hay turnos para mostrar. Agrega integrantes a la tanda primero.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(count = totalParticipantes) { index ->
            val pNumber = index + 1
            val fechaP = CalculadoraTanda.calcularFechaPeriodo(tanda.fechaInicio, pNumber, tanda.periodicidad)
            val quienCobra = CalculadoraTanda.quienCobraEnPeriodo(uiState.participantes, pNumber)
            val formatter = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))

            val (estadoTexto, backgroundColor, textColor) = when {
                pNumber < pActual -> Triple("Cobrado / Pasado", VerdeClaroContraste, VerdeOscuro)
                pNumber == pActual -> Triple("Le toca hoy", DoradoSuave, Color(0xFF6B4E00))
                else -> Triple("Próximo", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (pNumber == pActual) DoradoAcento else VerdePrimario
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$pNumber",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = quienCobra?.nombre ?: "Sin asignar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = fechaP.format(formatter).replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = estadoTexto,
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
