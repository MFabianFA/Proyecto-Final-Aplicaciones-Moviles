package com.example.proyectofinaltandaclara.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.proyectofinaltandaclara.ui.screens.lista.TandaResumenUiState
import com.example.proyectofinaltandaclara.ui.theme.DoradoAcento
import com.example.proyectofinaltandaclara.ui.theme.DoradoSuave
import com.example.proyectofinaltandaclara.ui.theme.VerdeOscuro

/**
 * Tarjeta resumen para mostrar una Tanda en la pantalla principal.
 */
@Composable
fun TandaCard(
    resumen: TandaResumenUiState,
    onClick: () -> Unit,
    onGestionarParticipantes: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Fila Superior: Nombre y Badge de Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = resumen.tanda.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerdeOscuro,
                    modifier = Modifier.weight(1f)
                )

                EstadoBadge(
                    totalAdeudos = resumen.totalAdeudos,
                    estaTerminada = resumen.estaTerminada
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información de Monto y Periodicidad
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = resumen.tanda.montoPorPersona.toFormatoMoneda(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " / persona",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = resumen.tanda.periodicidad.tituloHtml,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Avance del periodo
            val textoAvance = if (resumen.numeroParticipantes == 0) {
                "Sin participantes agregados"
            } else {
                "Periodo ${resumen.periodoActual} de ${resumen.numeroParticipantes}"
            }

            Text(
                text = "Avance: $textoAvance",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cuadro destacado: A quién le toca cobrar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DoradoSuave)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Le toca cobrar: ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B4E00)
                    )
                    Text(
                        text = resumen.quienCobraActual,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DoradoAcento
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fila de acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onGestionarParticipantes) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Gestionar Integrantes",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar Tanda",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
