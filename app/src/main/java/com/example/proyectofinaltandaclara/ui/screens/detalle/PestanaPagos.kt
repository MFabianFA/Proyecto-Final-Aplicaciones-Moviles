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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
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
import com.example.proyectofinaltandaclara.data.model.Participante
import com.example.proyectofinaltandaclara.ui.components.toFormatoMoneda
import com.example.proyectofinaltandaclara.ui.theme.DoradoAcento
import com.example.proyectofinaltandaclara.ui.theme.DoradoSuave
import com.example.proyectofinaltandaclara.ui.theme.RojoError
import com.example.proyectofinaltandaclara.ui.theme.RojoSuave
import com.example.proyectofinaltandaclara.ui.theme.VerdeClaroContraste
import com.example.proyectofinaltandaclara.ui.theme.VerdeOscuro
import com.example.proyectofinaltandaclara.ui.theme.VerdePrimario
import com.example.proyectofinaltandaclara.utils.CalculadoraTanda
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pestaña 1: Pagos.
 * Permite seleccionar el periodo, muestra el acumulado reunido y ofrece
 * casillas de verificación para registrar o cancelar pagos por participante.
 */
@Composable
fun PestanaPagos(
    uiState: DetalleTandaUiState,
    onSeleccionarPeriodo: (Int) -> Unit,
    onTogglePago: (Long, Int) -> Unit
) {
    val tanda = uiState.tanda ?: return
    val totalParticipantes = uiState.participantes.size
    val periodoSel = uiState.periodoSeleccionado

    if (totalParticipantes == 0) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Agrega integrantes a la tanda para registrar y consultar sus pagos.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val pagosDelPeriodo = uiState.pagos.filter { it.periodo == periodoSel }
    val idsPagados = pagosDelPeriodo.map { it.participanteId }.toSet()

    val numeroPagados = idsPagados.size
    val numeroPendientes = totalParticipantes - numeroPagados

    val montoReunido = numeroPagados * tanda.montoPorPersona
    val montoObjetivo = totalParticipantes * tanda.montoPorPersona

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Selector horizontal de periodos
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count = totalParticipantes) { index ->
                val pNumber = index + 1
                val fechaP = CalculadoraTanda.calcularFechaPeriodo(tanda.fechaInicio, pNumber, tanda.periodicidad)
                val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es", "MX"))

                val esPeriodoActual = (pNumber == uiState.periodoActual)
                val labelTexto = if (esPeriodoActual) "Periodo $pNumber (Hoy)" else "Periodo $pNumber"

                FilterChip(
                    selected = (pNumber == periodoSel),
                    onClick = { onSeleccionarPeriodo(pNumber) },
                    label = {
                        Column {
                            Text(text = labelTexto, fontWeight = if (pNumber == periodoSel) FontWeight.Bold else FontWeight.Normal)
                            Text(text = fechaP.format(formatter), fontSize = 11.sp)
                        }
                    }
                )
            }
        }

        // Tarjeta resumen del periodo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VerdeClaroContraste)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reunido en Periodo $periodoSel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = VerdeOscuro
                    )
                    Text(
                        text = "${montoReunido.toFormatoMoneda()} / ${montoObjetivo.toFormatoMoneda()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VerdePrimario
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val textoFaltantes = if (numeroPendientes == 0) {
                    "¡Fondo reunido por completo! Todos pagaron este periodo."
                } else {
                    "Faltan $numeroPendientes de $totalParticipantes personas por pagar"
                }

                Text(
                    text = textoFaltantes,
                    fontSize = 13.sp,
                    color = if (numeroPendientes > 0) RojoError else VerdeOscuro,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Lista de participantes con Checkbox
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = uiState.participantes,
                key = { it.id }
            ) { participante ->
                val estaPagado = idsPagados.contains(participante.id)
                val esQuienCobra = (participante.turno == periodoSel)

                ParticipantePagoCard(
                    participante = participante,
                    estaPagado = estaPagado,
                    esQuienCobra = esQuienCobra,
                    onTogglePago = { onTogglePago(participante.id, periodoSel) }
                )
            }
        }
    }
}

@Composable
fun ParticipantePagoCard(
    participante: Participante,
    estaPagado: Boolean,
    esQuienCobra: Boolean,
    onTogglePago: () -> Unit
) {
    val backgroundColor = when {
        esQuienCobra -> DoradoSuave
        !estaPagado -> RojoSuave
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = estaPagado,
                onCheckedChange = { onTogglePago() },
                colors = CheckboxDefaults.colors(
                    checkedColor = VerdePrimario,
                    uncheckedColor = RojoError
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = participante.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (esQuienCobra) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DoradoAcento)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Le toca cobrar",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = if (estaPagado) "Pagado" else "Pendiente",
                color = if (estaPagado) VerdePrimario else RojoError,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
