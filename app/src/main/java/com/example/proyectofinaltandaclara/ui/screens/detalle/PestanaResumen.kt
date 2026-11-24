package com.example.proyectofinaltandaclara.ui.screens.detalle

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinaltandaclara.ui.components.toFormatoMoneda
import com.example.proyectofinaltandaclara.ui.theme.RojoError
import com.example.proyectofinaltandaclara.ui.theme.RojoSuave
import com.example.proyectofinaltandaclara.ui.theme.VerdeClaroContraste
import com.example.proyectofinaltandaclara.ui.theme.VerdeOscuro
import com.example.proyectofinaltandaclara.ui.theme.VerdePrimario
import com.example.proyectofinaltandaclara.utils.CalculadoraTanda
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pestaña 3: Resumen.
 * Totales globales de la tanda, desglose detallado de adeudos por persona y
 * botón para compartir el reporte del periodo actual con el grupo vía Intent nativo.
 */
@Composable
fun PestanaResumen(
    uiState: DetalleTandaUiState
) {
    val tanda = uiState.tanda ?: return
    val context = LocalContext.current
    val totalParticipantes = uiState.participantes.size
    val totalPagosRegistrados = uiState.pagos.size
    val totalPagosProyectados = totalParticipantes * totalParticipantes

    val montoTotalPeriodo = tanda.montoPorPersona * totalParticipantes
    val montoGranTotalTanda = montoTotalPeriodo * totalParticipantes

    val fechaFin = if (totalParticipantes > 0) {
        CalculadoraTanda.calcularFechaPeriodo(tanda.fechaInicio, totalParticipantes, tanda.periodicidad)
    } else {
        tanda.fechaInicio
    }

    val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))

    val adeudosMap = CalculadoraTanda.calcularAdeudos(
        uiState.participantes,
        uiState.pagos,
        tanda.fechaInicio,
        tanda.periodicidad
    )

    val participantesConAdeudo = uiState.participantes.mapNotNull { p ->
        val periodosAdeudados = adeudosMap[p.id] ?: emptyList()
        if (periodosAdeudados.isNotEmpty()) {
            val montoTotalAdeudo = periodosAdeudados.size * tanda.montoPorPersona
            AdeudoPersonaEstado(p, periodosAdeudados, montoTotalAdeudo)
        } else null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón destacado: Compartir con el grupo
        item {
            Button(
                onClick = {
                    val textoInforme = generarTextoInformeGrupo(uiState)
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, textoInforme)
                        putExtra(Intent.EXTRA_SUBJECT, "Informe Tanda - ${tanda.nombre}")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Compartir informe con el grupo")
                    context.startActivity(shareIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Compartir estado con el grupo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tarjeta de Totales Generales
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Totales Generales",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeOscuro
                    )

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Aporte individual:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(tanda.montoPorPersona.toFormatoMoneda(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Integrantes (Periodos):", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$totalParticipantes personas", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bolsa entregada por periodo:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(montoTotalPeriodo.toFormatoMoneda(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VerdePrimario)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Monto total circulante:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(montoGranTotalTanda.toFormatoMoneda(), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = VerdeOscuro)
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Fecha inicio:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(tanda.fechaInicio.format(formatter), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Fecha fin estimada:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(fechaFin.format(formatter), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pagos acumulados:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$totalPagosRegistrados de $totalPagosProyectados pagos", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Sección de Desglose de Adeudos por Persona
        item {
            Text(
                text = "Estado de Adeudos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro
            )
        }

        if (participantesConAdeudo.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VerdeClaroContraste)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "¡Excelente! No hay adeudos pendientes en esta tanda.",
                            color = VerdeOscuro,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(participantesConAdeudo) { itemAdeudo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RojoSuave)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = itemAdeudo.participante.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val periodosTexto = itemAdeudo.periodosAdeudados.joinToString(", ") { "Periodo $it" }
                            Text(
                                text = "Debe: $periodosTexto",
                                fontSize = 12.sp,
                                color = RojoError,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RojoError)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = itemAdeudo.montoAdeudoTotal.toFormatoMoneda(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Genera el texto formateado para compartir el estado actual de la tanda por WhatsApp / SMS.
 */
private fun generarTextoInformeGrupo(uiState: DetalleTandaUiState): String {
    val tanda = uiState.tanda ?: return ""
    val participantes = uiState.participantes
    val pActual = uiState.periodoActual
    val fechaP = CalculadoraTanda.calcularFechaPeriodo(tanda.fechaInicio, pActual, tanda.periodicidad)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "MX"))

    val quienCobra = CalculadoraTanda.quienCobraEnPeriodo(participantes, pActual)?.nombre ?: "Sin asignar"
    val montoPorPersona = tanda.montoPorPersona
    val totalMontoCobro = montoPorPersona * participantes.size

    val pagosDelPeriodo = uiState.pagos.filter { it.periodo == pActual }
    val idsPagados = pagosDelPeriodo.map { it.participanteId }.toSet()

    val yaPagaron = participantes.filter { idsPagados.contains(it.id) }.map { it.nombre }
    val faltanPorPagar = participantes.filter { !idsPagados.contains(it.id) }.map { it.nombre }

    val proximoTurno = CalculadoraTanda.quienCobraEnPeriodo(participantes, pActual + 1)?.nombre ?: "Fin de tanda"

    return buildString {
        appendLine("📌 *INFORME DE TANDA: ${tanda.nombre.uppercase()}*")
        appendLine("📅 *Periodo:* $pActual (${fechaP.format(formatter)})")
        appendLine()
        appendLine("💰 *A quien le toca cobrar:* $quienCobra")
        appendLine("💵 *Monto a recibir:* ${totalMontoCobro.toFormatoMoneda()}")
        appendLine()
        appendLine("✅ *Ya pagaron (${yaPagaron.size}):*")
        if (yaPagaron.isEmpty()) {
            appendLine("  • Nadie ha pagado aún")
        } else {
            yaPagaron.forEach { appendLine("  • $it") }
        }
        appendLine()
        appendLine("⚠️ *Faltan por pagar (${faltanPorPagar.size}):*")
        if (faltanPorPagar.isEmpty()) {
            appendLine("  • ¡Todos al corriente!")
        } else {
            faltanPorPagar.forEach { appendLine("  • $it (${montoPorPersona.toFormatoMoneda()})") }
        }
        appendLine()
        appendLine("➡️ *Próximo turno (Periodo ${pActual + 1}):* $proximoTurno")
        appendLine()
        append("Enviado desde TandaClara 📲")
    }
}
