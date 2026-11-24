package com.example.proyectofinaltandaclara.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinaltandaclara.ui.theme.RojoError
import com.example.proyectofinaltandaclara.ui.theme.RojoSuave
import com.example.proyectofinaltandaclara.ui.theme.VerdeClaroContraste
import com.example.proyectofinaltandaclara.ui.theme.VerdePrimario

/**
 * Insignia de estado que muestra si la tanda está "Al corriente" (verde),
 * "X adeudos" (roja) o "Terminada" (gris).
 */
@Composable
fun EstadoBadge(
    totalAdeudos: Int,
    estaTerminada: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, texto) = when {
        estaTerminada -> Triple(
            Color(0xFFE0E0E0),
            Color(0xFF616161),
            "Terminada"
        )
        totalAdeudos > 0 -> Triple(
            RojoSuave,
            RojoError,
            if (totalAdeudos == 1) "1 adeudo" else "$totalAdeudos adeudos"
        )
        else -> Triple(
            VerdeClaroContraste,
            VerdePrimario,
            "Al corriente"
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = texto,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
